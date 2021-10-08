package com.example.manager.config.shiro;

import com.example.manager.result.ResultEntity;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class ShiroExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResultEntity<Void> unauthenticatedHandler(AuthenticationException e) {
        return ResultEntity.fail(e.getCause());
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResultEntity<Void> unauthorizedHandler(AuthorizationException e) {
        return ResultEntity.fail(e.getCause());
    }

}
