package com.example.file.utils;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author ：chicunxiang
 * @date ：Created in 2021/9/30 09:34
 * @description：
 * @version: 1.0
 */
public class AESUtils {

    public static final String PASSWORD = "BF80F5E91D6D27D1E053294C0B0ACD5D";

    public static String getIV() {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecureRandom sha1PRNG = SecureRandom.getInstance("SHA1PRNG");
            byte[] iv = new byte[cipher.getBlockSize()];
            sha1PRNG.nextBytes(iv);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            return Base64.encodeToString(ivParams.getIV());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPassword() {

        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            // kg.init(128);//要生成多少位，只需要修改这里即可128, 192或256
            //SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以生成的秘钥就一样。
            kg.init(128, new SecureRandom(PASSWORD.getBytes()));
            SecretKey sk = kg.generateKey();
            byte[] b = sk.getEncoded();
            return Base64.encodeToString(b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("没有此算法。");
        }
        return "";
    }


    /**
     * 解密工具直接放进去即可
     */
    public static String decryptS5(String sSrc, String sKey, String ivParameter) {
        try {
            byte[] raw = Base64.decode(sKey);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(Base64.decode(ivParameter));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] myendicod = Base64.decode(sSrc);
            byte[] original = cipher.doFinal(myendicod);
            return new String(original);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 解密工具直接放进去即可
     */
    public static String encryptS5(String sSrc, String sKey, String ivParameter) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec skeySpec = new SecretKeySpec(Base64.decode(sKey), "AES");
            IvParameterSpec iv = new IvParameterSpec(Base64.decode(ivParameter));//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes());
            return Base64.encodeToString(encrypted);//此处使用BASE64做转码。
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(decryptS5("v+MhUEE9CNTM6SR67PfMKA==",
                "juaKV3JyFdAFL0bTJzQQiQ==", "NjQbpuh0YSBGaGMwEzBllw=="));
        System.out.println(decryptS5("jp2/cUct0531OoXokc3tpidetUoKKpHaQc79CyguYtMmvM9/XMmgWzTlNx/GN8ZV7JY20Iv6O+3ABMCnvg0fKQTq1zmKuK7lJb6tgrJVbtgtySJDG/3LTwL0q9vUjrXpMgwkrVzsGwthgcTJgF8G4F6umItnzvnb9uScGqeWBVgj2tSblvBUEvVsBdJr4XBX8ZH6rpRXSWOkWmnB5lUF0Oo03gpI9SqcyquQVOtIRYURKtok4jXrnK3pp7Z+ehM1kpccpLGLMzQF0L9MDIkJqGTT6D6B5x4fn38AH3wXirrc40VW+JJ8mps1GDzXldvoE0VHU22swhKG2EyYjbJRxkTDKcMW7DQmk6ibVaCS6GmgV5m7w4iQJgciIxcc4W0N80W0XUQPJY/HqnENNwD+lpBKHvxChN7GX4d9ZRz5YLZnWesF6MrtQRH6XXIgq/Jjp/syDdLaR6GU14+c61mdKg==",
                 "x6y8pPqaRFAmcn3varj0Fg==", "GFCDCLG79EMn8YcOHoY7Ew=="));
    }
}
