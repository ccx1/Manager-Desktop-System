package com.example.manager.utils;


import com.example.manager.enums.HttpStatus;
import com.example.manager.result.CodeException;
import org.springframework.util.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FileUtils {


    public static List<File> listFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return Arrays.asList(file.listFiles());
        }
        return Collections.EMPTY_LIST;
    }


    public static List<File> getRootLists() {
        File[] files = File.listRoots();
        if (files != null && files.length > 0) {
            return Arrays.asList(files);
        }
        return Collections.EMPTY_LIST;
    }


    //    public static ResponseEntity<FileSystemResource> export(File file) {
//        if (file == null) {
//            return null;
//        }
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getName()));
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//        headers.add("ETag", String.valueOf(System.currentTimeMillis()));
//        return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType("application/octet-stream;charset=UTF-8")).body(new FileSystemResource(file));
//    }
    public static byte[] imageToBytes(BufferedImage bImage, String format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, format, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     * @return
     */
    private static boolean checkExistsAndInit(String path, String initData) {
        if (Files.notExists(Paths.get(path))) {
            try {
                Files.createFile(Paths.get(path));
                writeDataToFile(path, initData);
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 写入数据到文件
     *
     * @param path
     * @param data
     */
    public static void writeDataToFile(String path, String data) {
        try {
            checkExistsAndInit(path, "");

            Files.write(Paths.get(path), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件
     *
     * @param path 路径
     * @param def  不存在返回的数据
     * @return
     */
    public static String readFileData(String path, String def) {
        try {
            if (!checkExistsAndInit(path, def)) {
                return def;
            }
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是不是图片
     *
     * @param file
     * @return
     */
    public static boolean isImage(File file) {
        String[] img = {"JPG", "JPEG", "GIF", "BMP", "PNG", "WEBP", "SVG"};
        for (String s : img) {
            String name = file.getName();
            int i = name.lastIndexOf(".");
            if (i == -1 || file.getName().length() < s.length()) {
                return false;
            }
            if (s.equals(name.substring(i + 1).toUpperCase())) {
                return true;
            }
        }
        return false;

    }

    public static final String generatorTempZipName(File file) {
        String join = String.join("-", System.currentTimeMillis() + "",
                file.getAbsolutePath(),
                file.getName(),
                new Random().nextInt(1000) + "");
        return DigestUtils.md5DigestAsHex((join).getBytes()) + ".zip";
    }


    public static void main(String[] args) {

    }

    public static void deleteFile(File file) {
//判断文件不为null或文件目录存在
        if (file == null || !file.exists()) {
            throw new CodeException(HttpStatus.FILE_NOT_FIND);
        }
        List<File> files = listFile(file.getAbsolutePath());
        for (File f : files) {
            if (f.isDirectory()) {
                deleteFile(f);
            } else {
                f.delete();
            }
        }
        file.delete();
    }


}
