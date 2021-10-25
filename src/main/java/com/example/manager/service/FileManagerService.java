package com.example.manager.service;

import com.example.manager.dto.FileInfoDto;
import com.example.manager.dto.UserDto;
import com.example.manager.enums.HttpCode;
import com.example.manager.factory.UserFactory;
import com.example.manager.result.CodeException;
import com.example.manager.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
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

    public Map<String, Object> getFileInfo(UserDto userDto, String id) {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isBlank(id)) {
            if (userDto.getUsername().equals("admin")) {
                List<File> rootLists = FileUtils.getRootLists();
                result.put("fileInfo", parseFileList(rootLists));
                result.put("currentPath", "");
            } else if (userDto.isSystemPathAdmin()) {
                List<File> rootLists = FileUtils.listFile(userDto.getPath());
                result.put("fileInfo", parseFileList(rootLists));
                result.put("currentPath", AESUtils.encryptS5(userDto.getPath(), UserFactory.getUserKey(), UserFactory.getUserIv()));
            } else {
                try {
                    String projectPath = new File("").getCanonicalPath();

                    File f = new File(projectPath, MD5.md5(userDto.getUsername()));

                    result.put("fileInfo", parseFileList(FileUtils.listFile(f.getAbsolutePath())));
                    result.put("currentPath", AESUtils.encryptS5(f.getAbsolutePath(), UserFactory.getUserKey(), UserFactory.getUserIv()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            String filePath = AESUtils.decryptS5(id, userDto.getKey(), userDto.getIv());
            if (StringUtils.isBlank(filePath)) {
                result.put("fileInfo", Collections.EMPTY_LIST);
            }
            List<File> files = FileUtils.listFile(filePath);
            result.put("fileInfo", parseFileList(files));
            result.put("currentPath", id);
        }

        return result;
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
        fileInfoDto.setId(AESUtils.encryptS5(path, UserFactory.getUserKey(), UserFactory.getUserIv()));
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
            throw new CodeException(HttpCode.UN_KNOW_ERROR);
        }
        String filePath = AESUtils.decryptS5(id, UserFactory.getUserKey(), UserFactory.getUserIv());
        if (StringUtils.isBlank(filePath)) {
            throw new CodeException(HttpCode.FILE_NOT_FIND);
        }
        return new File(filePath);
    }

    public void upload(String id, MultipartFile multipartFile) throws IOException {
        String filePath = AESUtils.decryptS5(id, UserFactory.getUserKey(), UserFactory.getUserIv());
        if (StringUtils.isBlank(filePath)) {
            throw new CodeException(HttpCode.FILE_NOT_FIND);
        }
        File file = new File(filePath);
        if (file.isFile()) {
            throw new CodeException(HttpCode.FILE_UPLOAD_PATH_IS_FILES);
        }
        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new CodeException(HttpCode.FILE_UPLOAD_FILE_NAME_EMPTY);
        }
        File temp = new File(file, originalFilename);
        if (temp.exists()) {
            String extension = FileUtils.getExtension(originalFilename);
            String name = FileUtils.onlyName(file.getAbsolutePath(), originalFilename, extension, 1);
            temp = new File(file, name);
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

    public void unZipFile(String id) {
        String filePath = AESUtils.decryptS5(id, UserFactory.getUserKey(), UserFactory.getUserIv());
        if (StringUtils.isBlank(filePath)) {
            throw new CodeException(HttpCode.FILE_NOT_FIND);
        }

        File file = new File(filePath);
        // 获取父类文件夹
        String parent = file.getParent();

        try {
            ZipUtils.unZip(filePath, parent);
        } catch (Exception e) {
            throw new CodeException(HttpCode.UN_ZIP_FILE_FAIL);
        }
    }

    public void rename(String targetId, String name) {
        String filePath = AESUtils.decryptS5(targetId, UserFactory.getUserKey(), UserFactory.getUserIv());
        if (StringUtils.isBlank(filePath)) {
            throw new CodeException(HttpCode.FILE_NOT_FIND);
        }

        File file = new File(filePath);
        String extension = FileUtils.getExtension(name);
        String onlyName = FileUtils.onlyName(file.getParent(), name, extension, 1);
        try {
            Files.move(Paths.get(filePath), Paths.get(new File(file.getParent(), onlyName).getAbsolutePath()),
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void move(String originId, String targetId) {
        String targetFilePath = AESUtils.decryptS5(targetId, UserFactory.getUserKey(), UserFactory.getUserIv());
        if (StringUtils.isBlank(targetFilePath)) {
            throw new CodeException(HttpCode.FILE_NOT_FIND);
        }
        String originFilePath = AESUtils.decryptS5(originId, UserFactory.getUserKey(), UserFactory.getUserIv());
        if (StringUtils.isBlank(originFilePath)) {
            throw new CodeException(HttpCode.FILE_NOT_FIND);
        }
        File file = new File(targetFilePath, new File(originFilePath).getName());
        if (file.exists()) {
            throw new CodeException(HttpCode.TARGET_DIR_FILE_EXITS_FAIL);
        }
        try {
            Files.move(Paths.get(originFilePath), Paths.get(file.getAbsolutePath()),
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void recycle(List<String> ids) {
        // 回收站思路, 1. 移动文件. 2. 文件原路径记录. 3. 文件不可操作
        try {
            String projectPath = new File("").getCanonicalPath();
            File f = new File(projectPath, MD5.md5("recycle"));
            // 获取到回收站的文件列表
            File currentTimeDir = new File(f, new SimpleDateFormat("yyyyMMdd").format(new Date()));
            if (!currentTimeDir.exists()) {
                currentTimeDir.mkdirs();
            }
            for (String id : ids) {
                String targetFilePath = AESUtils.decryptS5(id, UserFactory.getUserKey(), UserFactory.getUserIv());
                // 将文件移动到回收站
                File file = new File(targetFilePath);
                String extension = FileUtils.getExtension(file.getName());
                String onlyName = FileUtils.onlyName(currentTimeDir.getAbsolutePath(), file.getName(), extension, 1);
                Files.copy(Paths.get(targetFilePath), Paths.get(new File(currentTimeDir,onlyName).getAbsolutePath()), StandardCopyOption.COPY_ATTRIBUTES);
                Files.delete(Paths.get(targetFilePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


