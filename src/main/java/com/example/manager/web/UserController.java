package com.example.manager.web;

import com.example.manager.dto.UserDto;
import com.example.manager.result.ResultEntity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/login")
    public ResultEntity<String> login(String username, String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        subject.login(token);
        return ResultEntity.ok(String.valueOf(subject.getSession().getId()));
    }


    @GetMapping("/loginCas")
    public void loginCas(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(contextPath + "/index");
    }


    @RequiresAuthentication
    @GetMapping("/logout")
    public ResultEntity<Void> login() {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return ResultEntity.ok();
    }

    @RequiresAuthentication
    @GetMapping("/info")
    public ResultEntity<UserDto> getUserInfo() {
        Subject subject = SecurityUtils.getSubject();
        UserDto userTempDto = (UserDto) subject.getPrincipal();
        UserDto target = new UserDto();
        BeanUtils.copyProperties(userTempDto, target);
        target.setPassword("********");
        return ResultEntity.ok(target);

    }

}
