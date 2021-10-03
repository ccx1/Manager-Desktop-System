package com.example.file.service;

import com.example.file.dto.FileInfoDto;
import com.example.file.enums.HttpStatus;
import com.example.file.result.CodeException;
import com.example.file.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author ：chicunxiang
 * @date ：Created in 2021/9/29 16:41
 * @description：
 * @version: 1.0
 */
@Service
public class FileManagerService {

    private static final String FOLDER_PATH = "folder";
    private static final String FILE_PATH = "file";
    private static final String BASE_PATH = "static/file_extend_imgs/ic-{type}.png";
    private static final Map<String, String> IMAGE_MAP = new HashMap<>();
    private String mSKey = "juaKV3JyFdAFL0bTJzQQiQ==";
    private String mIV = "NjQbpuh0YSBGaGMwEzBllw==";

    public List<FileInfoDto> getFileInfo(String id) {
        if (StringUtils.isBlank(id)) {
            List<File> rootLists = FileUtils.getRootLists();
            return parseFileList(rootLists);
        }
        String filePath = AESUtils.decryptS5(id, mSKey, mIV);
        if (StringUtils.isBlank(filePath)) {
            return Collections.EMPTY_LIST;
        }
        List<File> files = FileUtils.listFile(filePath);
        return parseFileList(files);
    }

    private List<FileInfoDto> parseFileList(List<File> files) {
        if (files != null) {
            List<FileInfoDto> fileList = new ArrayList<>();
            for (File file : files) {
                fileList.add(createFileInfo(file));
            }
            return fileList;
        }
        return Collections.EMPTY_LIST;
    }

    private FileInfoDto createFileInfo(File file) {
        FileInfoDto fileInfoDto = new FileInfoDto();
        String path = file.getAbsolutePath();
        fileInfoDto.setName(StringUtils.isBlank(file.getName()) ? file.getAbsolutePath() : file.getName());
        fileInfoDto.setFile(file.isFile());
        fileInfoDto.setId(AESUtils.encryptS5(path, mSKey, mIV));
        fileInfoDto.setSize(file.isFile() ? file.length() : 0);

        String extend = FILE_PATH;
        if (file.isFile()) {
            String name = file.getName();
            int spotIndex = name.lastIndexOf(".");
            if (spotIndex > -1 && (name.length() - (spotIndex + 1) != 0)) {
                extend = name.substring(name.lastIndexOf(".") + 1);
            }
        } else {
            extend = FOLDER_PATH;
        }

        Resource resource = new ClassPathResource(BASE_PATH.replace("{type}", extend));
        if (!resource.exists()) {
            if (file.isFile()) {
                resource = new ClassPathResource(BASE_PATH.replace("{type}", FILE_PATH));
            } else if (file.isDirectory()) {
                resource = new ClassPathResource(BASE_PATH.replace("{type}", FOLDER_PATH));
            }
        }

        String imgStr = IMAGE_MAP.get(resource.getFilename());
        if (StringUtils.isBlank(imgStr)) {
            try {
                imgStr = "data:image/png;base64," + ImgBase64.getImgStr(resource.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!StringUtils.isBlank(imgStr)) {
                IMAGE_MAP.put(resource.getFilename(), imgStr);
            }
        }
        fileInfoDto.setExtendImage(imgStr);
        return fileInfoDto;
    }

    public void deleteFile(List<String> ids) {
        for (String id : ids) {
            File file = checkTargetId(id);
            FileUtils.deleteFile(file);
        }
    }

    private File checkTargetId(String id) {
        if (StringUtils.isBlank(id)) {
            throw new CodeException(HttpStatus.UN_KNOW_ERROR);
        }
        String filePath = AESUtils.decryptS5(id, mSKey, mIV);
        if (StringUtils.isBlank(filePath)) {
            throw new CodeException(HttpStatus.FILE_NOT_FIND);
        }
        return new File(filePath);
    }

    public void upload(String id, MultipartFile multipartFile) throws IOException {
        String filePath = AESUtils.decryptS5(id, mSKey, mIV);
        if (StringUtils.isBlank(filePath)) {
            throw new CodeException(HttpStatus.FILE_NOT_FIND);
        }
        File file = new File(filePath);
        if (file.isFile()) {
            throw new CodeException(HttpStatus.FILE_UPLOAD_PATH_IS_FILES);
        }
        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new CodeException(HttpStatus.FILE_UPLOAD_FILE_NAME_EMPTY);
        }
        File temp = new File(file, originalFilename);
        if (temp.exists()) {
            throw new CodeException(HttpStatus.FILE_UPLOAD_FILE_EXITS);
        }
        multipartFile.transferTo(temp);
        System.gc();
    }

    public void downloadFile(List<String> ids, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();


        String name = "归档.zip";
        if (ids.size() == 1) {
            name = checkTargetId(ids.get(0)).getName();
        }
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(name) + "\"");

        if (ids.size() > 1) {
            List<String> fileList = new ArrayList<>();
            for (String id : ids) {
                fileList.add(checkTargetId(id).getAbsolutePath());
            }
            ZipUtils.createZipFile(fileList, outputStream);
        } else {
            File file = checkTargetId(ids.get(0));
            if (file.isDirectory()) {
                ZipUtils.toZip(file.getAbsolutePath(), outputStream, true);
            } else {
                FileInputStream fileInputStream = new FileInputStream(file);
                int len;
                byte[] buff = new byte[8086];
                while ((len = fileInputStream.read(buff)) != -1) {
                    outputStream.write(buff, 0, len);
                }
            }
        }

    }
}
