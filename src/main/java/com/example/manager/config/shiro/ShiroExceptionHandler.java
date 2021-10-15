package com.example.manager.config.shiro;

import com.example.manager.result.CodeException;
import com.example.manager.result.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class ShiroExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResultEntity<Void> unauthenticatedHandler(AuthenticationException e) {
        log.error("权限不足 unauthenticatedHandler:", e);
        return ResultEntity.fail(new CodeException(400, "身份验证异常, 请重新登录"));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResultEntity<Void> unauthorizedHandler(AuthorizationException e) {
        log.error("权限不足 unauthorizedHandler:", e);
        return ResultEntity.fail(new CodeException(4020, "您没有权限访问此接口"));
    }

}
