package com.example.file.utils;

import com.example.file.dto.ProcessDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author ：chicunxiang
 * @date ：Created in 2021/9/30 11:31
 * @description：
 * @version: 1.0
 */
public class ProcessUtils {
    private static BufferedReader createExec(String cmd) throws IOException {
        return new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(cmd).getInputStream(), Charset.forName("GBK")));
    }

    public static String readString(BufferedReader bufferedReader) {
        try {
            String buffer = "";
            StringBuilder stringBuffer = new StringBuilder();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public static int stopProcess(int pid) {
        try {
            BufferedReader exec = createExec("kill -9 " + pid);
            String resule = readString(exec);
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String runCommand(String command) {
        try {
            BufferedReader exec = createExec(command);
            return readString(exec);
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public static List<ProcessDto> listAllProcess() {
        String osName = System.getProperties().getProperty("os.name");

        List<ProcessDto> result = new ArrayList<>();
        Process exec = null;
        BufferedReader bufferedReader = null;
        try {
            if (osName.equals("Linux")) {
                exec = Runtime.getRuntime().exec("ps -ef");
            } else {
                exec = Runtime.getRuntime().exec("netstat -ano");
            }
            InputStream inputStream = exec.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream,Charset.forName("GBK")));
            String buffer = "";
            int line = 0;
            while ((buffer = bufferedReader.readLine()) != null) {
                if (line != 0) {
                    result.add(analysis(buffer));
                }
                line += 1;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(result, (o1, o2) -> o1.getPid() - o2.getPid());
        Collections.reverse(result);
        return result;
    }

    private static ProcessDto analysis(String row) {
        int pos = 0;
        int nextStartPos = 0;

        boolean isReadSpace = true;
        List<Object> result = new ArrayList<>();
        while (pos < row.length()) {
            char c = row.charAt(pos);
            if (isReadSpace && c == 32) {
                result.add(row.substring(nextStartPos, pos));
                isReadSpace = false;
                nextStartPos = pos;
            }
            if (!isReadSpace && c != 32) {
                nextStartPos = pos;
                if (result.size() == 7) {
                    result.add(row.substring(nextStartPos, row.length()));
                    break;
                }
                isReadSpace = true;
            }
            pos++;
        }
        return new ProcessDto(result.get(0).toString(),
                Integer.valueOf(result.get(1).toString()),
                Integer.valueOf(result.get(2).toString()),
                Integer.valueOf(result.get(3).toString()),
                result.get(4).toString(),
                result.get(5).toString(),
                result.get(6).toString(),
                result.get(7).toString());

    }
}
