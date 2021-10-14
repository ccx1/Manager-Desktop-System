package com.example.manager.web;

import com.example.manager.dto.FileInfoDto;
import com.example.manager.dto.UserDto;
import com.example.manager.result.ResultEntity;
import com.example.manager.service.FileManagerService;
import com.example.manager.utils.FileUtils;
import com.example.manager.utils.ZipUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author ：chicunxiang
 * @date ：Created in 2021/9/29 16:27
 * @description：
 * @version: 1.0
 */
@RestController
@RequestMapping("file")
public class FileManagerController {

    @Autowired
    private FileManagerService mFileManagerService;


    @GetMapping("/list")
    @RequiresPermissions("file:list")
    public ResultEntity<Map<String, Object>> listFile(@RequestParam(value = "targetId", required = false) String id) {
        Subject subject = SecurityUtils.getSubject();
        UserDto userDto = (UserDto) subject.getPrincipal();
        try {
            id = URLDecoder.decode(id, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, Object> result = mFileManagerService.getFileInfo(userDto, id);
        return ResultEntity.ok(result);
    }


    @PostMapping("/delete")
    @RequiresPermissions("file:delete")
    public ResultEntity<Void> delete(@RequestBody Map<String, Object> params) {
        mFileManagerService.deleteFile((List<String>) params.get("targetIds"));
        return ResultEntity.ok();
    }


    @GetMapping("/download")
    @RequiresPermissions("file:download")
    @RequiresAuthentication
    public void download(@RequestParam(value = "targetIds") List<String> ids, HttpServletResponse response) throws IOException {
        mFileManagerService.downloadFile(ids, response);
    }


    @PostMapping("/upload")
    @RequiresPermissions("file:upload")
    public ResultEntity<Void> upload(@RequestParam(value = "targetId") String id,
                                     @RequestParam("file")
                                             MultipartFile multipartFile
    ) throws IOException {
        mFileManagerService.upload(id, multipartFile);
        return ResultEntity.ok();
    }

    @PostMapping("/unZip")
    @RequiresPermissions("file:unzip")
    public ResultEntity<Void> unZip(@RequestBody Map<String, String> params) throws IOException {
        // 解压文件
        mFileManagerService.unZipFile(params.get("targetId"));
        return ResultEntity.ok();
    }


    @PostMapping("/rename")
    @RequiresPermissions("file:rename")
    public ResultEntity<Void> rename(@RequestBody Map<String, String> params) throws IOException {
        // 重命名文件
        mFileManagerService.rename(params.get("targetId"), params.get("newName"));
        return ResultEntity.ok();
    }


    @PostMapping("/move")
    @RequiresPermissions("file:move")
    public ResultEntity<Void> move(@RequestBody Map<String, String> params) throws IOException {
        // 重命名文件
        mFileManagerService.move(params.get("originId"), params.get("targetId"));
        return ResultEntity.ok();
    }


}
