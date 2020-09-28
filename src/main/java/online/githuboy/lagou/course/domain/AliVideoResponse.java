package online.githuboy.lagou.course.domain;

import java.util.List;

public class AliVideoResponse {

    /**
     * VideoBase : {"Status":"Normal","VideoId":"f8aa89a0c48a4026b31827b4c28ea7ba","TranscodeMode":"NoTranscode","CreationTime":"2020-07-09T09:32:14Z","Title":"私有加密_安卓 03.mp4","MediaType":"video","CoverURL":"https://vod.lagou.com/image/cover/22C9AE82524F4F6DBB4CBEDFAC6EA222-6-2.png","Duration":"1101.9493","OutputType":"cdn"}
     * RequestId : D9572C51-D9DB-440C-8E65-50B3C16533E7
     * PlayInfoList : {"PlayInfo":[{"Status":"Normal","StreamType":"video","Size":239617256,"Definition":"OD","Fps":"23.976025","Duration":"1101.9493","ModificationTime":"2020-07-09T09:32:14Z","Specification":"Original","Bitrate":"1739.588","Encrypt":0,"PreprocessStatus":"UnPreprocess","Format":"mp4","PlayURL":"https://vod.lagou.com/sv/4e65bd8-17332eb186c/4e65bd8-17332eb186c.mp4","NarrowBandType":"0","CreationTime":"2020-07-09T09:32:14Z","Height":1080,"Width":1920,"JobId":"f8aa89a0c48a4026b31827b4c28ea7ba02"}]}
     */

    public VideoBaseBean VideoBase;
    public String RequestId;
    public PlayInfoListBean PlayInfoList;
    public String Code;
    public String Message;
    public String Recommend;
    public String HostId;

    public static class VideoBaseBean {
        /**
         * Status : Normal
         * VideoId : f8aa89a0c48a4026b31827b4c28ea7ba
         * TranscodeMode : NoTranscode
         * CreationTime : 2020-07-09T09:32:14Z
         * Title : 私有加密_安卓 03.mp4
         * MediaType : video
         * CoverURL : https://vod.lagou.com/image/cover/22C9AE82524F4F6DBB4CBEDFAC6EA222-6-2.png
         * Duration : 1101.9493
         * OutputType : cdn
         */

        public String Status;
        public String VideoId;
        public String TranscodeMode;
        public String CreationTime;
        public String Title;
        public String MediaType;
        public String CoverURL;
        public String Duration;
        public String OutputType;
    }

    public static class PlayInfoListBean {
        public List<PlayInfoBean> PlayInfo;

        public static class PlayInfoBean {
            /**
             * Status : Normal
             * StreamType : video
             * Size : 239617256
             * Definition : OD
             * Fps : 23.976025
             * Duration : 1101.9493
             * ModificationTime : 2020-07-09T09:32:14Z
             * Specification : Original
             * Bitrate : 1739.588
             * Encrypt : 0
             * PreprocessStatus : UnPreprocess
             * Format : mp4
             * PlayURL : https://vod.lagou.com/sv/4e65bd8-17332eb186c/4e65bd8-17332eb186c.mp4
             * NarrowBandType : 0
             * CreationTime : 2020-07-09T09:32:14Z
             * Height : 1080
             * Width : 1920
             * JobId : f8aa89a0c48a4026b31827b4c28ea7ba02
             */

            public String Status;
            public String StreamType;
            public int Size;
            public String Definition;
            public String Fps;
            public String Duration;
            public String ModificationTime;
            public String Specification;
            public String Bitrate;
            public int Encrypt;
            public String PreprocessStatus;
            public String Format;
            public String PlayURL;
            public String NarrowBandType;
            public String CreationTime;
            public int Height;
            public int Width;
            public String JobId;
        }
    }
}
