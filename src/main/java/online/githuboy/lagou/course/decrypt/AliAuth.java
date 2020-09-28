package online.githuboy.lagou.course.decrypt;

public class AliAuth {

    /**
     * SecurityToken : CAIS3wJ1q6Ft5B2yfSjIr5DPcujEr+5KxJK4amzBiUoCettLjazahjz2IH9IdHVoAO8fvvU0m2tY7PsZlrMqFcYVHBeVPJUsssgHrF/xJpLFst2J6r8JjsUPjPcDr1ipsvXJasDVEfkuE5XEMiI5/00e6L/+cirYXD7BGJaViJlhQ80KVw2jF1RvD8tXIQ0Qk619K3zdZ9mgLibui3vxCkRv2HBijm8txqmj/MyQ5x31i1v0y+B3wYHtOcqca8B9MY1WTsu1vohzarGT6CpZ+jlM+qAU6qlY4mXrs9qHEkFNwBiXSZ22lOdiNwhkfKM3NrdZpfzn751Ct/fUip78xmQmX4gXcVyGFd7wkZOZQrzzbY1kLu6iARmXjIDTbKuSmhg/fHcWODlNf9ccMXJqAXQuMGqFdv/7oAmVO1fyEPfegPtrioAJ5lHp7MeMGV+DeLyQyh0EIaU7a044msGUb9NfpcQagAFu9d7NiSS7GHhH4wh2GWf6CwLke1MsazCJKICMACoW8gLRqPcCASc6x2anClbt7lcU+sdGyhsoIb2Uxqeb2e4a+9aI3zO6/4Yxboi4AAl5zM3peft+sZ7euua4knZLi0ZBE6C+boRhUFog8vk9ZIy09v15w4uRWbRYCLwwpraIVQ==
     * AuthInfo : {"CI":"xRs797x5TfGTY5QBE5GvREyZGuIFU9bWQSUZ2RhDZrFHgyUOVmSxV/FvE08RmnR9FnywfjILNa3/gW5nwYjF1KvmluMjeJZzPS45neqol2I=","Caller":"kK+6AHeORrwOKdsU28WnRaA41Usz7lgCY+m+BIBX9eQ=","ExpireTime":"2020-09-27T09:42:16Z","MediaId":"f8aa89a0c48a4026b31827b4c28ea7ba","PlayDomain":"vod.lagou.com","Signature":"NXT1Nl+pdCH2xygyvsPoJvfo1fw="}
     * VideoMeta : {"Status":"Normal","VideoId":"f8aa89a0c48a4026b31827b4c28ea7ba","Title":"私有加密_安卓 03.mp4","CoverURL":"https://vod.lagou.com/image/cover/22C9AE82524F4F6DBB4CBEDFAC6EA222-6-2.png","Duration":1101.9493}
     * AccessKeyId : STS.NSz9RpB1ksPShJpmJWvTgbiqd
     * PlayDomain : vod.lagou.com
     * AccessKeySecret : BVtAZifETX7XD2VijjdxzcBGFzgtNzEKmcRMeGsYyHxi
     * Region : cn-shanghai
     * CustomerId : 1618139964448548
     */

    public String AccessKeyId;
    public String AuthInfo;
    public String AccessKeySecret;
    public String SecurityToken;
    public VideoMetaBean VideoMeta;
    public String PlayDomain;
    public String Region;
    public long CustomerId;

    public static class VideoMetaBean {
        /**
         * Status : Normal
         * VideoId : f8aa89a0c48a4026b31827b4c28ea7ba
         * Title : 私有加密_安卓 03.mp4
         * CoverURL : https://vod.lagou.com/image/cover/22C9AE82524F4F6DBB4CBEDFAC6EA222-6-2.png
         * Duration : 1101.9493
         */

        public String Status;
        public String VideoId;
        public String Title;
        public String CoverURL;
        public double Duration;
    }
}
