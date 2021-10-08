package com.example.manager.utils;



import java.io.*;

/**
 * @author ：chicunxiang
 * @date ：Created in 2021/9/29 17:45
 * @description：
 * @version: 1.0
 */
public class ImgBase64 {
    /**
     * 将图片转换成Base64编码
     * @param imgFile 待处理图片
     * @return
     */
    public static String getImgStr(InputStream in) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理

        byte[] data = null;
        // 读取图片字节数组
        try {
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(data);
    }

    /**
     * 对字节数组字符串进行Base64解码并生成图片
     * @param imgStr 图片数据
     * @param imgFilePath 保存图片全路径地址
     * @return
     */
    public static boolean generateImage(String imgStr, String imgFilePath) {
        //
        if (imgStr == null) // 图像数据为空
            return false;
        try {
            // Base64解码
            byte[] decode = Base64.decode(imgStr);
            // 生成jpg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(decode);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
