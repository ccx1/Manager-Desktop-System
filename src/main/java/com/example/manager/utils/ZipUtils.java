package com.example.manager.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private static final int BUFFER_SIZE = 2 * 1024;

    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure) {

        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
            long end = System.currentTimeMillis();
/*
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
*/
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException ignored) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            FileInputStream in = null;
            try {
                zos.putNextEntry(new ZipEntry(name));
                int len;
                in = new FileInputStream(sourceFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
            } finally {
                zos.closeEntry();
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (KeepDirStructure) {
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    if (KeepDirStructure) {
                        compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure);
                    }
                }
            }
        }
    }


    public static void createZipFile(List<String> fileList, OutputStream outputStream) {

        if (fileList == null || fileList.size() == 0) {
            return;
        }

        //构建压缩文件File
        //初期化ZIP流
        ZipOutputStream out = null;

        try {
            //构建ZIP流对象
            out = new ZipOutputStream(outputStream);
            //循环处理传过来的集合
            for (int i = 0; i < fileList.size(); i++) {
                //获取目标文件
                File inFile = new File(fileList.get(i));
                if (inFile.exists()) {
                    //定义ZipEntry对象
                    ZipEntry entry = new ZipEntry(inFile.getName());
                    //赋予ZIP流对象属性
                    out.putNextEntry(entry);
                    int len = 0;
                    //缓冲
                    byte[] buffer = new byte[1024];
                    //构建FileInputStream流对象
                    FileInputStream fis;
                    fis = new FileInputStream(inFile);
                    while ((len = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    //关闭closeEntry
                    out.closeEntry();
                    //关闭FileInputStream
                    fis.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //最后关闭ZIP流
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 解压缩
     * @param sZipPathFile 要解压的文件
     * @param sDestPath 解压到某文件夹
     * @return
     */
    public static void unZip(String sZipPathFile, String sDestPath) {
        try {
            // 先指定压缩档的位置和档名，建立FileInputStream对象
            FileInputStream fins = new FileInputStream(sZipPathFile);
            // 将fins传入ZipInputStream中
            ZipInputStream zins = new ZipInputStream(fins, Charset.forName("GBK"));
            ZipEntry ze = null;
            byte[] ch = new byte[256];
            while ((ze = zins.getNextEntry()) != null) {
                File zfile = new File(sDestPath, ze.getName());
                File fpath = new File(zfile.getParentFile().getPath());
                if (ze.isDirectory()) {
                    if (!zfile.exists())
                        zfile.mkdirs();
                    zins.closeEntry();
                } else {
                    if (!fpath.exists())
                        fpath.mkdirs();
                    FileOutputStream fouts = new FileOutputStream(zfile);
                    int i;
                    while ((i = zins.read(ch)) != -1)
                        fouts.write(ch, 0, i);
                    zins.closeEntry();
                    fouts.close();
                }
            }
            fins.close();
            zins.close();
        } catch (Exception e) {
            System.err.println("Extract error:" + e.getMessage());
        }
    }

}
