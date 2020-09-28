package online.githuboy.lagou.course;

import com.alibaba.fastjson.JSONObject;
import online.githuboy.lagou.course.domain.CourseDetailList;
import online.githuboy.lagou.course.domain.LessonInfo;
import online.githuboy.lagou.course.task.VideoInfoLoader;
import online.githuboy.lagou.course.utils.HttpUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 下载器
 *
 * @author suchu
 * @since 2019年8月2日
 */
public class Downloader {
    private final static String COURSE_INFO_API = "https://gate.lagou.com/v1/neirong/kaiwu/getCourseLessons?courseId=%s";
    /**
     * 拉钩视频课程地址
     */
    public String courseId;
    /**
     * 视频保存路径
     */

    public String savePath;

    private File basePath;

    private String courseUrl;

    private CountDownLatch latch;
    private List<LessonInfo> lessonInfoList = new ArrayList<>();
    private volatile List<MediaLoader> mediaLoaders;

    private long start;

    public Downloader(String courseId, String savePath) {
        this.courseId = courseId;
        this.savePath = savePath;
        this.courseUrl = String.format(COURSE_INFO_API, courseId);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        if (checkFFMPEG()) return;
        String courseId = "251";
        String savePath = "E:\\lagou";
        Downloader downloader = new Downloader(courseId, savePath);
        Thread logThread = new Thread(() -> {
            while (true) {
                System.out.println(String.format("Thread pool:%s", ExecutorService.getExecutor()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }, "log-thread");
        logThread.setDaemon(true);
        //logThread.start();
        downloader.start();
    }

    private static boolean checkFFMPEG() {
        try {
            System.out.println("检查ffmpeg是否存在");
            CmdExecutor.executeCmd(new File("."), "ffmpeg", "-version");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return true;
        }
        return false;
    }

    public void start() throws IOException, InterruptedException {
        start = System.currentTimeMillis();
        parseLessonInfo2();
        parseVideoInfo();
        downloadMedia();

    }

    private void parseLessonInfo2() throws IOException {
        String strContent = HttpUtils
                .get(courseUrl, CookieStore.getCookie())
                .header("x-l-req-header", " {deviceType:1}")
                .execute().body();

        CourseDetailList courseDetailList = JSONObject.parseObject(strContent, CourseDetailList.class);
        if (courseDetailList.state != 1) {
            throw new RuntimeException("访问课程信息出错:" + strContent);
        }
        System.out.println("rsp : " + strContent);

        this.basePath = new File(savePath, this.courseId + "_" + courseDetailList.content.courseName);
        if (!basePath.exists()) {
            basePath.mkdirs();
        }

        for (CourseDetailList.ContentBean.CourseSectionListBean sectionListBean : courseDetailList.content.courseSectionList) {
            for (CourseDetailList.ContentBean.CourseSectionListBean.CourseLessonsBean courseLesson : sectionListBean.courseLessons) {
                if (courseLesson.videoMediaDTO != null) {
                    LessonInfo lessonInfo=  new LessonInfo(courseLesson.id,courseLesson.theme,courseLesson.appId);
                    lessonInfo.fileId = courseLesson.videoMediaDTO.fileId;
                    lessonInfo.fileEdk = courseLesson.videoMediaDTO.fileEdk;
                    lessonInfo.fileUrl = courseLesson.videoMediaDTO.fileUrl;
                    lessonInfoList.add(lessonInfo);
                }

                System.out.println(String.format("解析到课程信息：name：%s,appId:%s,fileId:%s", courseLesson.theme, courseLesson.appId,
                        courseLesson.videoMediaDTO == null ? "未发布" : courseLesson.videoMediaDTO.fileId));
            }
        }
        System.out.println(1);
    }

    private void parseVideoInfo() {
        latch = new CountDownLatch(lessonInfoList.size());
        mediaLoaders = new Vector<>();
        lessonInfoList.forEach(lessonInfo -> {
            VideoInfoLoader loader = new VideoInfoLoader(lessonInfo.lessonName, lessonInfo.appId, lessonInfo.fileId,
                    lessonInfo.fileUrl, lessonInfo.lessionId);
            loader.setM3U8MediaLoaders(mediaLoaders);
            loader.setBasePath(this.basePath);
            loader.setLatch(latch);
            ExecutorService.execute(loader);
        });
    }

    private void downloadMedia() throws InterruptedException {
        latch.await();
        if (mediaLoaders.size() != lessonInfoList.size()) {
            System.out.println(String.format("视频META信息没有全部下载成功: success:%s,total:%s", mediaLoaders.size(), lessonInfoList.size()));
            tryTerminal();
            return;
        }
        System.out.println(String.format("所有视频META信息获取成功 total：%s", mediaLoaders.size()));
        CountDownLatch all = new CountDownLatch(mediaLoaders.size());

        for (MediaLoader loader : mediaLoaders) {
            loader.setLatch(all);
            ExecutorService.getExecutor().execute(loader);
        }
        all.await();
        long end = System.currentTimeMillis();
        System.out.println(String.format("所有视频处理完成:%s s", (end - start) / 1000));
        System.out.println(String.format("视频输出目录:%s", this.basePath.getAbsolutePath()));
        System.out.println("\n\n失败统计信息\n\n");
        Stats.failedCount.forEach((key, value) -> System.out.println(key + " -> " + value.get()));
        tryTerminal();
    }

    private void tryTerminal() throws InterruptedException {
        System.out.println(String.format("程序将在%s s后退出", 5));
        ExecutorService.getExecutor().shutdown();
        ExecutorService.getHlsExecutor().shutdown();
        ExecutorService.getHlsExecutor().awaitTermination(5, TimeUnit.SECONDS);
        ExecutorService.getExecutor().awaitTermination(5, TimeUnit.SECONDS);
    }

}
