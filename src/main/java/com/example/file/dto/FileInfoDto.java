package com.example.file.dto;

import lombok.Data;

/**
 * @author ：chicunxiang
 * @date ：Created in 2021/9/29 16:47
 * @description：
 * @version: 1.0
 */
@Data
public class FileInfoDto {

    private String name;

    private long size;

    private String id;

    private boolean isFile;

    private String extendImage;
}
