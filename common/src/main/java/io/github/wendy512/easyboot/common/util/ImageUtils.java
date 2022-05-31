package io.github.wendy512.easyboot.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * base64 工具类
 * @author taowenwu
 * @date 2021-08-10 22:00:22:00
 * @since 1.0.0
 */
public class ImageUtils {


    public static final String TYPE_JPG = "jpg";
    public static final String TYPE_GIF = "gif";
    public static final String TYPE_PNG = "png";
    public static final String TYPE_BMP = "bmp";
    public static final String TYPE_UNKNOWN = "unknown";

    /**
     * 图片转化成base64字符串
     *
     * @return
     */
    public static String getImageBase64(InputStream inputStream) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        // 返回Base64编码过的字节数组字符串
        String encode = null;
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        try {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            encode = encoder.encode(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return encode;
    }

    /**
     * base64字符串转化成图片
     *
     * @param imageBase64
     *            图片编码
     * @return
     * @throws IOException
     */
    @SuppressWarnings("finally")
    public static byte[] generateImage(String imageBase64) {
        // 对字节数组字符串进行Base64解码并生成图片
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] bytes = decoder.decodeBuffer(imageBase64);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }

            return bytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     *
     * @description: 根据文件流判断图片类型
     * @author: Jeff
     * @date: 2019年12月7日
     * @return
     */
    public static String getPicType(byte[] imageBytes) {
        try {
            byte[] b = Arrays.copyOfRange(imageBytes, 0, 4);
            String type = bytesToHexString(b).toUpperCase();
            if (type.contains("FFD8FF")) {
                return TYPE_JPG;
            } else if (type.contains("89504E47")) {
                return TYPE_PNG;
            } else if (type.contains("47494638")) {
                return TYPE_GIF;
            } else if (type.contains("424D")) {
                return TYPE_BMP;
            } else {
                return TYPE_UNKNOWN;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @description: byte数组转换成16进制字符串
     * @author: Jeff
     * @date: 2019年12月7日
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }
}
