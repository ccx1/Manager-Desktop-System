package com.example.manager.config.shiro;

import com.alibaba.fastjson.JSON;
import com.example.manager.enums.HttpCode;
import com.example.manager.result.CodeException;
import com.example.manager.result.ResultEntity;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ：chicunxiang
 * @date ：Created in 2021/10/15 08:45
 * @description：
 * @version: 1.0
 */
public class ShiroLoginFilter extends FormAuthenticationFilter {

    /**
     * 如果isAccessAllowed返回false 则执行onAccessDenied
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (request instanceof HttpServletRequest) {
            if (((HttpServletRequest) request).getMethod().toUpperCase().equals("OPTIONS")) {
                return true;
            }
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }

    /**
     * 在访问controller前判断是否登录，返回json，不进行重定向。
     *
     * @param request
     * @param response
     * @return true-继续往下执行，false-该filter过滤器已经处理，不继续执行其他过滤器
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (!isLoginRequest(request, response)) {
            if (isAjax(httpRequest)) {
                // httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                //这里是个坑，如果不设置的接受的访问源，那么前端都会报跨域错误，因为这里还没到corsConfig里面
                httpServletResponse.setHeader("Access-Control-Allow-Origin", ((HttpServletRequest) request).getHeader("Origin"));
                httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setContentType("application/json");
                httpServletResponse.getWriter().write(JSON.toJSONString(ResultEntity.fail(new CodeException(HttpCode.NO_LOGIN))));
            } else {
                // redirectToLogin(request, response);
                saveRequestAndRedirectToLogin(request, response);
            }

            return false;
        }
        return true;
    }

    /**
     * 判断ajax请求
     *
     * @param request
     * @return
     */
    boolean isAjax(HttpServletRequest request) {
        return (request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString()));
    }
}
