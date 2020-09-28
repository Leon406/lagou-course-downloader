package online.githuboy.lagou.course.task;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import online.githuboy.lagou.course.ExecutorService;
import online.githuboy.lagou.course.MediaLoader;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 视频metaInfo 加载器
 *
 * @author suchu
 * @since 2019年8月3日
 */
@Slf4j
public class VideoInfoLoader implements Runnable, NamedTask {
    /**
     * 0-> appId
     * 1-> fileId;
     */
    private static final String API_TEMPLATE = "https://gate.lagou.com/v1/neirong/kaiwu/getCourseLessonDetail?lessonId={0}";
    private final static int maxRetryCount = 3;
    private final String videoName;
    private String appId;
    private final String fileId;
    private final String fileUrl;
    private final String lessonId;
    private int retryCount = 0;
    @Setter
    private File basePath;
    @Setter
    public String mediaType = "mp4";
    @Setter
    private List<MediaLoader> m3U8MediaLoaders;

    @Setter
    private CountDownLatch latch;

    public VideoInfoLoader(String videoName, String appId, String fileId, String fileUrl, String lessonId) {
        this.videoName = videoName;
        this.appId = appId;
        this.fileId = fileId;
        this.fileUrl = fileUrl;
        this.lessonId = lessonId;
    }

    @Override
    public void run() {
        try {
            log.info("获取视频{},m3u8地址成功:{}", videoName, fileUrl);
            if ("m3u8".equals(mediaType)) {
                M3U8MediaLoader m3U8 = new M3U8MediaLoader(fileUrl, videoName, basePath.getAbsolutePath(), fileId);
                m3U8MediaLoaders.add(m3U8);
            } else if ("mp4".equals(mediaType)) {
                MP4Downloader mp4Downloader = MP4Downloader.builder().appId(appId).basePath(basePath.getAbsoluteFile()).videoName(videoName).fileId(fileId).lessonId(lessonId).build();
                m3U8MediaLoaders.add(mp4Downloader);
            }
            latch.countDown();
        } catch (Exception e) {
            log.error("获取视频{}信息失败:", videoName, e);
            if (retryCount < maxRetryCount) {
                retryCount += 1;
                log.info("第{}次重试获取:{}", retryCount, videoName);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                ExecutorService.execute(this);
            } else {
                log.info(" video{}最大重试结束:{}", videoName, maxRetryCount);
                latch.countDown();
            }
        }
    }

    @Override
    public String getTaskDescription() {
        return videoName;
    }
}
