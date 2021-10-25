package com.example.manager.timeTask;

import com.example.manager.utils.FileUtils;
import com.example.manager.utils.MD5;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author ：chicunxiang
 * @date ：Created in 2021/10/19 10:54
 * @description：
 * @version: 1.0
 */
public class RecycleTask {


    @Scheduled(cron = "0 30 9 * * ?") // 每天早上9点半/
    public void recycle() {

        try {
            String projectPath = new File("").getCanonicalPath();
            File f = new File(projectPath, MD5.md5("recycle"));

            List<File> files = FileUtils.listFile(f.getAbsolutePath());
            long currentTimeMillis = System.currentTimeMillis();
            for (File file : files) {
                long yyyyMMdd = new SimpleDateFormat("yyyyMMdd").parse(file.getName()).getTime();
                if (currentTimeMillis - yyyyMMdd > (1000L * 60 * 60 *24 * 30)) {
                    FileUtils.deleteFile(file);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

}
