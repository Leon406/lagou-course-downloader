package online.githuboy.lagou.course.decrypt;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import online.githuboy.lagou.course.CookieStore;
import online.githuboy.lagou.course.domain.LessonPlayHistory;
import online.githuboy.lagou.course.utils.HttpUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author suchu
 * @date 2020/8/6
 * @link https://help.aliyun.com/document_detail/56124.html?spm=a2c4g.11186623.2.30.14487fbfjBfxAC
 * @link https://www.alibabacloud.com/help/zh/doc-detail/108840.htm
 */
public class AliPlayerDecrypt {


    /**
     * 解析authInfo
     */
    public static AliAuth parseAuthInfo(String auth) {
        String authJson = new String(Base64.getDecoder().decode(auth), Charset.forName("iso8859-1"));
        return JSONObject.parseObject(authJson, AliAuth.class);
    }

    public static Map<String, String> generateCommonParams() {
        Map<String, String> params = new HashMap<>();
        params.put("Timestamp", generateTimestamp());
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureVersion", "1.0");
        params.put("SignatureNonce", generateRandom());
        params.put("Format", "JSON");
        params.put("Version", "2017-03-21");
//        params.put("AuthTimeout", "7200");
//        params.put("Definition", "240");
//        params.put("PlayConfig", "{}");
//        params.put("ReAuthInfo", "{}");
        params.put("Action", "GetPlayInfo");
        return params;
    }

    public static String generateSignature(String cqs, String accessKeySecret) throws UnsupportedEncodingException {
        //构造签名字符串
        String stringToSign =
                "GET&" + percentEncode("/") + "&" +
                        percentEncode(cqs);
        byte[] bytes = hmacSHA1Signature(accessKeySecret, stringToSign);

        System.out.println(stringToSign);
        System.out.println(String.format("cqs: %s \nkey: %s", cqs, accessKeySecret));
        System.out.println(Base64.getEncoder().encodeToString(bytes));

        return Base64.getEncoder().encodeToString(bytes);
    }

    public static void getMp4() {
        Map<String, String> publicParam = generateCommonParams();

        publicParam.put("VideoId", "5608ab1e4fd24d23b2add9c58a8ce6cc");
        //编码参数 https://www.alibabacloud.com/help/zh/doc-detail/108840.htm
        List<String> allParams = getAllParams(publicParam);

        //构造规范化请求字符串。
        String cqs = getCQS(allParams);
        System.out.println(cqs);
        //构造签名字符串
        String queryString = cqs;
        String api = "https://vod.cn-shanghai.aliyuncs.com/?" + queryString;
        String body1 = HttpRequest.get(api).execute().body();
        System.out.println(api);
        System.out.println("\n\nAPI request result:\n" + body1);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        getMp4();

        decrypt();
    }

    private static void decrypt() throws UnsupportedEncodingException {
        String body = HttpUtils.get("https://gate.lagou.com/v1/neirong/kaiwu/getLessonPlayHistory?lessonId=1857&isVideo=true",
                CookieStore.getCookie())
                .header("x-l-req-header", "{deviceType:1}")
                .execute()
                .body();

        LessonPlayHistory playHistory = JSON.parseObject(body, LessonPlayHistory.class);
        System.out.println(body);
        if (playHistory.state != 1) throw new RuntimeException(body);
        String auth = playHistory.content.mediaPlayInfoVo.aliPlayAuth;
        System.out.println(auth);
        AliAuth aliAuth = parseAuthInfo(auth);
        Map<String, String> publicParam = generateCommonParams();
        publicParam.put("AccessKeyId", aliAuth.AccessKeyId);
        publicParam.put("AuthInfo", aliAuth.AuthInfo);
        publicParam.put("SecurityToken", aliAuth.SecurityToken);
        publicParam.put("VideoId", playHistory.content.mediaPlayInfoVo.fileId);

        //编码参数 https://www.alibabacloud.com/help/zh/doc-detail/108840.htm
        List<String> allParams = getAllParams(publicParam);

        //构造规范化请求字符串。
        String cqs = getCQS(allParams);
        System.out.println(cqs);
        //构造签名字符串
        String queryString = cqs + "&Signature=" + generateSignature(cqs, aliAuth.AccessKeySecret);
        String api = "https://vod.cn-shanghai.aliyuncs.com/?" + queryString;
        String body1 = HttpRequest.get(api).execute().body();
        System.out.println(api);
        System.out.println("\n\nAPI request result:\n" + body1);
    }

    /*特殊字符替换为转义字符*/
    public static String percentEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8")
                    .replace("+", "%20")
                    .replace("%7E", "~")
                    .replace("*", "%2A");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }

    /*对所有参数名称和参数值做URL编码*/
    public static List<String> getAllParams(Map<String, String> params) {
        List<String> encodeParams = new ArrayList<>();
        if (params != null) {
            encodeMaps(params, encodeParams);
        }

        return encodeParams;
    }

    private static void encodeMaps(Map<String, String> privateParams, List<String> encodeParams) {
        for (String key : privateParams.keySet()) {
            String value = privateParams.get(key);
            //将参数和值都urlEncode一下。
            String encodeKey = percentEncode(key);
            String encodeVal = percentEncode(value);
            encodeParams.add(encodeKey + "=" + encodeVal);
        }
    }

    /*获取 CanonicalizedQueryString*/
    public static String getCQS(List<String> allParams) {
        Collections.sort(allParams);
        return allParams.toString().replaceAll("\\[|]", "").replace(", ", "&");
    }

    /**
     * 计算签名时，RFC2104规定的Key值是您的AccessKeySecret并加上与号（&)，
     */
    public static byte[] hmacSHA1Signature(String accessKeySecret, String stringToSign) {
        try {
            String key = accessKeySecret + "&";
            try {
                SecretKeySpec signKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
                Mac mac = Mac.getInstance("HmacSHA1");
                mac.init(signKey);
                return mac.doFinal(stringToSign.getBytes());
            } catch (Exception e) {
                throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
            }
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /*生成当前UTC时间戳Time*/
    private static String generateTimestamp() {
        Date date = new Date();
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }

    private static String generateRandom() {
        return UUID.randomUUID().toString();
    }
}
