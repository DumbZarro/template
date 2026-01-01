package top.dumbzarro.template.common.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class SignUtil {

    public static Map<String, String> buildHeaders(String secret, byte[] body) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String nonce = UUID.randomUUID().toString();
        String appKey = "sc-overseas-warehouse";

        // 全小写 排序
        SortedMap<String, String> param = new TreeMap<>();
        param.put("bodyhash", sha256Hex(Optional.ofNullable(body).orElse(new byte[0])));
        param.put("x-erp-appkey", appKey);
        param.put("x-erp-nonce", nonce);
        param.put("x-erp-timestamp", timestamp);

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Erp-AppKey", appKey);
        headers.put("X-Erp-Nonce", nonce);
        headers.put("X-Erp-Timestamp", timestamp);
        headers.put("X-Erp-Version", "1.0.0");
        headers.put("X-Erp-Sign", generateSignature(param, secret));

        return headers;
    }

    public static String generateSignature(Map<String, String> param, String appSecret) {
        StringJoiner signParam = new StringJoiner("&");
        for (Map.Entry<String, String> entry : param.entrySet()) {
            signParam.add(entry.getKey() + "=" + entry.getValue());
        }
        return hmacSha256(signParam.toString(), appSecret);
    }

    private static String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return toHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 加密失败", e);
        }
    }

    private static String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return toHex(md.digest(data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) hex.append(String.format("%02x", b));
        return hex.toString();
    }

}