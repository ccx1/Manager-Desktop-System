package com.example.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ：chicunxiang
 * @date ：Created in 2021/9/30 11:31
 * @description：
 * @version: 1.0
 */
@AllArgsConstructor
@Data
public class ProcessDto {
    private String uid;
    private Integer pid;

    private Integer  ppid;

    private Integer c;

    private String stime;

    private String tty;

    private String time;

    private String cmd;
}
