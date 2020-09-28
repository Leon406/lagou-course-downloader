package online.githuboy.lagou.course.task;

import cn.hutool.core.io.StreamProgress;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import online.githuboy.lagou.course.CookieStore;
import online.githuboy.lagou.course.ExecutorService;
import online.githuboy.lagou.course.MediaLoader;
import online.githuboy.lagou.course.Stats;
import online.githuboy.lagou.course.decrypt.AliAuth;
import online.githuboy.lagou.course.domain.AliVideoResponse;
import online.githuboy.lagou.course.domain.LessonPlayHistory;
import online.githuboy.lagou.course.utils.HttpUtils;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static online.githuboy.lagou.course.decrypt.AliPlayerDecrypt.*;

/**
 * MP4下载器
 *
 * @author suchu
 * @date 2020/8/7
 */
@Builder
@Slf4j
public class MP4Downloader implements Runnable, NamedTask, MediaLoader {
    private static final String API_TEMPLATE = "https://gate.lagou.com/v1/neirong/kaiwu/getLessonPlayHistory?lessonId={0}&isVideo=true";

    private final static int maxRetryCount = 3;
    private String videoName;
    private String appId;
    private String fileId;
    private String fileUrl;
    private String lessonId;
    private volatile int retryCount = 0;
    @Setter
    private File basePath;

    private File workDir;
    @Setter
    private CountDownLatch latch;

    public String getLegalVideoName() {
        return videoName.replaceAll("/|\\|", "_");
    }

    private void initDir() {
        workDir = new File(basePath, getLegalVideoName() + "_" + lessonId);
        if (!workDir.exists()) {
            workDir.mkdirs();
        }
    }

    @Override
    public void run() {
        initDir();
        File file = checkFileExist();
        if (file == null) return;
        String url = MessageFormat.format(API_TEMPLATE, this.lessonId);
        try {
            log.info("获取视频{},信息，url：{}", lessonId, url);
            String body = HttpUtils.get(url, CookieStore.getCookie()).header("x-l-req-header", "{deviceType:1}").execute().body();

            System.out.println(body);
            LessonPlayHistory playHistory = JSON.parseObject(body, LessonPlayHistory.class);

            if (playHistory.state != 1) throw new RuntimeException(body);

            AliAuth aliAuth = parseAuthInfo(playHistory.content.mediaPlayInfoVo.aliPlayAuth);
            Map<String, String> publicParam = generateCommonParams();
            publicParam.put("AccessKeyId", aliAuth.AccessKeyId);
            publicParam.put("AuthInfo", aliAuth.AuthInfo);
            publicParam.put("SecurityToken", aliAuth.SecurityToken);
            publicParam.put("VideoId", playHistory.content.mediaPlayInfoVo.fileId);
            List<String> allParams = getAllParams(publicParam);
            //构造规范化请求字符串。
            String cqs = getCQS(allParams);
            //构造签名字符串
            String queryString = cqs + "&Signature=" + generateSignature(cqs,aliAuth.AccessKeySecret);
            String api = "https://vod.cn-shanghai.aliyuncs.com/?" + queryString;
            String body1 = HttpRequest.get(api).execute().body();
            System.out.println("\n\nAPI request result:\n\n" + body1);
            AliVideoResponse aliVideoResponse = JSON.parseObject(body1, AliVideoResponse.class);
            if (aliVideoResponse.Code != null) throw new RuntimeException("获取媒体信息失败");
            if (aliVideoResponse.PlayInfoList.PlayInfo.size() > 0) {
                String mp4Url = aliVideoResponse.PlayInfoList.PlayInfo.get(0).PlayURL;
                log.info("解析到MP4播放地址:{}", mp4Url);
                HttpRequest.get(mp4Url).execute().writeBody(file, new StreamProgress() {
                    @Override
                    public void start() {
                        System.out.println("开始下载视频:" + videoName);
                    }

                    @Override
                    public void progress(long l) {
                    }

                    @Override
                    public void finish() {
                        System.out.println("视频下载完成:" + videoName);
                        Stats.remove(videoName);
                        latch.countDown();
                    }
                });
            }
            //latch.countDown();
        } catch (Exception e) {
            log.error("获取视频{}信息失败:", videoName, e);
            if (retryCount < maxRetryCount) {
                Stats.incr(videoName);
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

    private File checkFileExist() {
        File file = new File(workDir, getLegalVideoName() + ".mp4");
        if (file.exists()) {
            log.info("视频已存在 " + file.getName());
            latch.countDown();
            return null;
        }
        log.info("视频 " + file.getName());
        return file;
    }
}
