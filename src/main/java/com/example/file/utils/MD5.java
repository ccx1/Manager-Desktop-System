package com.example.file.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MD5 {
    private MD5() {
    }

    public static String md5(String str) {
        if (str == null) {
            return "";
        } else {
            MessageDigest messageDigest = null;

            try {
                messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.reset();
                messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            } catch (NoSuchAlgorithmException var5) {
                var5.printStackTrace();
            }
            if (messageDigest == null) {
                return "";
            } else {
                byte[] byteArray = messageDigest.digest();
                StringBuilder md5StrBuff = new StringBuilder();

                for (byte b : byteArray) {
                    if (Integer.toHexString(255 & b).length() == 1) {
                        md5StrBuff.append("0").append(Integer.toHexString(255 & b));
                    } else {
                        md5StrBuff.append(Integer.toHexString(255 & b));
                    }
                }

                return md5StrBuff.toString();
            }
        }
    }

    public static String md5(byte[] target, int len) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(target, 0, len);

            byte[] byteArray = messageDigest.digest();
            StringBuilder md5StrBuff = new StringBuilder();

            for (byte b : byteArray) {
                if (Integer.toHexString(255 & b).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(255 & b));
                } else {
                    md5StrBuff.append(Integer.toHexString(255 & b));
                }
            }

            return md5StrBuff.toString();
        } catch (NoSuchAlgorithmException var6) {
            var6.printStackTrace();
        }
        return "";
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        } else {
            MessageDigest digest = null;
            FileInputStream in = null;
            byte[] buffer = new byte[1024];

            try {
                digest = MessageDigest.getInstance("MD5");
                in = new FileInputStream(file);

                while (true) {
                    int len;
                    if ((len = in.read(buffer, 0, 1024)) == -1) {
                        in.close();
                        break;
                    }

                    digest.update(buffer, 0, len);
                }
            } catch (Exception var6) {
                var6.printStackTrace();
                return null;
            }

            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        }
    }

    public static Map<String, String> getDirMD5(File file, boolean listChild) {
        if (!file.isDirectory()) {
            return null;
        } else {
            HashMap<String, String> map = new HashMap<>();
            File[] files = file.listFiles();

            assert files != null;
            for (File f : files) {
                if (f.isDirectory() && listChild) {
                    Map<String, String> dirMD5 = getDirMD5(f, listChild);
                    if (dirMD5 != null) {
                        map.putAll(dirMD5);
                    }
                } else {
                    String md5 = getFileMD5(f);
                    if (md5 != null) {
                        map.put(f.getPath(), md5);
                    }
                }
            }

            return map;
        }
    }
}
