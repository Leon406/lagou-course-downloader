package online.githuboy.lagou.course.domain;

public class LessonInfo {
    public String lessionId;
    public String lessonName;
    public String appId;
    public String fileId;
    public String fileUrl;
    public String fileEdk;

    public LessonInfo(String lessionId, String lessonName, String appId) {
        this.lessionId = lessionId;
        this.lessonName = lessonName;
        this.appId = appId;
    }
}
