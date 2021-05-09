package online.githuboy.lagou.course.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class CourseList {


    public Integer state;
    public String message;
    public ContentBean content;
    public Object uiMessage;

    public static class ContentBean {
        public MemberAdsBarBean memberAdsBar;
        public List<AllCoursePurchasedRecordBean> allCoursePurchasedRecord;
        public Object courseOrderSynEntry;

        public static class MemberAdsBarBean {
            public Integer identityCode;
            public String title;
            public String tips;
            public String buttonText;
            public String url;
        }

        public static class AllCoursePurchasedRecordBean {
            public Integer courseType;
            public String title;
            public List<BigCourseRecordListBean> bigCourseRecordList;
            public List<CourseRecord> courseRecordList;

            public static class BigCourseRecordListBean {
                public Integer id;
                public String name;
                public String h5Url;
                public String lastLearnLessonName;
                public String image;
                public Object openWebToStudy;
                public Boolean hasCourseWeChatGroup;
                public Object courseWeChatGroupTips;
                public String progressDesc;
                public Integer progressNum;
                public String bigCourseStartDate;
                public String bigCourseEndDate;
                public Boolean isShowBigCourseEndDate;
                public Boolean isStartCourse;
                public String serviceExpireTime;
                public Integer prepayProgress;
                public Integer source;
                public Object prepayTip;
                public Object prepayStatusTip;
                public Integer bigCourseType;
                public Object relationSmallCourseId;
                public Integer branchType;
                public Boolean minorCourse;

                @Override
                public String toString() {
                    return "BigCourseRecordListBean{" +
                            "id=" + id +
                            ", name='" + name + '\'' +
                            ", lastLearnLessonName='" + lastLearnLessonName + '\'' +
                            ", progressDesc='" + progressDesc + '\'' +
                            ", progressNum=" + progressNum +
                            ", serviceExpireTime='" + serviceExpireTime + '\'' +
                            '}';
                }
            }

            public static class CourseRecord {

                public Integer id;
                public String name;
                public String h5Url;
                public String lastLearnLessonName;
                public String image;
                public Object openWebToStudy;
                public Boolean hasCourseWeChatGroup;
                public String courseWeChatGroupTips;
                public String updateProgress;
                public Object updateTips;
                public Integer lessonUpdateNum;
                public Boolean isEnterpriseCourse;
                public Boolean isCampusCourse;
                public Boolean vipFreeCourse;
                public Object classCourseType;

                @Override
                public String toString() {
                    return "CourseRecord{" +
                            "id=" + id +
                            ", name='" + name + '\'' +
                            ", updateProgress='" + updateProgress + '\'' +
                            '}';
                }
            }
        }


    }
}
