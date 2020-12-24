package com.hongy.adbclient.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 签名加密工具类
 * Created by blw on 2016/8/26.
 */
public class SignAndEncrypt {


    //签名请求，返回生成的十六进制签名字符串
    public static String signRequest(Map<String, Object> params, String api_secret) {
        // 第一步：检查参数是否已经排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        query.append(api_secret);
        for (String key : keys) {
            String value = String.valueOf(params.get(key));
            if (value != null && value.length() > 0) {
                query.append(key).append(value);
            }
        }

        // 第三步：使用MD5加密
        byte[] bytes;
        query.append(api_secret);
        bytes = encryptMD5(query.toString());

        // 第四步：把二进制转化为大写的十六进制
        return byte2hex(bytes);
    }


    //敏感信息对称加密
    public static String DesEncrypt(String password, String api_secret) {
        String des_password = null;
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(Arrays.copyOfRange(api_secret.getBytes("UTF-8"), 0, 8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // 实例化加密类，参数为加密方式
            Cipher cipher = Cipher.getInstance("DES");
            // 初始化，此方法可以采用三种方式，按服务器要求来添加,第三个参数为SecureRandom
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            des_password = byte2hex(cipher.doFinal(password.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return des_password;

    }


    //解密
    public static String dESDecode(String password, String api_secret) {
        String plainText = null;

        try {
            SecureRandom random = new SecureRandom();

            byte[] desTextData = hexStringToBytes(password);
            byte[] secretData = Arrays.copyOf(api_secret.getBytes("UTF-8"), 8);

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            DESKeySpec dks = new DESKeySpec(secretData);
            SecretKey secretKey = keyFactory.generateSecret(dks);

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, random);

            byte decryptedData[] = cipher.doFinal(desTextData);
            plainText = new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return plainText;
    }


    //MD5非对称加密
    public static String asymmetricEncryptMd5(String data, String api_secret, String timestamp) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(api_secret);
        stringBuilder.append(data);
        stringBuilder.append(timestamp);
        stringBuilder.append(api_secret);
        return byte2hex(encryptMD5(stringBuilder.toString()));
    }


    //md5加密
    public static byte[] encryptMD5(String data) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            md5.update(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return md5.digest();
    }


    //字节数组转换为十六进制
    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    //获取时间戳
    public static String getTimeStamp() {
        long time = System.currentTimeMillis();//获取系统时间当前的时间戳
        String str = String.valueOf(time);
        return str;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];

        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }

        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
