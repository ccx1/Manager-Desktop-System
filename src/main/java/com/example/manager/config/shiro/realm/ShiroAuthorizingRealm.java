package com.example.manager.config.shiro.realm;

import com.example.manager.dto.UserDto;
import com.example.manager.enums.HttpStatus;
import com.example.manager.factory.UserFactory;
import com.example.manager.result.CodeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 授权相关服务-shiro
 *
 * @author qiguliuxing
 * @since 1.0.0
 */
@Component
@Slf4j
public class ShiroAuthorizingRealm extends AuthorizingRealm {


    //doGetAuthorizationInfo主要是获取用户的角色和权限，并交给Shiro去判断是否具有访问资源的权限
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        UserDto userDto = (UserDto) getAvailablePrincipal(principals);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 角色id
        // 角色名称
        Set<String> roles = userDto.getRoleNames();
        // 角色的权限组
        Set<String> permissions = userDto.getPermissions();

        info.setRoles(roles);
        info.setStringPermissions(permissions);

        return info;
    }

    //重写doGetAuthenticationInfo（用于登录验证）
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());

        if (StringUtils.isBlank(username)) {
            throw new AccountException("用户名不能为空");
        }
        if (StringUtils.isBlank(password)) {
            throw new AccountException("密码不能为空");
        }

        // 查询用户, 首先要有个地方去管理所有用户
        UserDto userDto = UserFactory.findUser(username);

        if (userDto == null) {
            throw new CodeException(HttpStatus.USER_NOT_EXIST_FAIL);
        }

        if (!password.equals(userDto.getPassword())) {
            throw new CodeException(HttpStatus.USER_PASSWORD_NOT_MATCH);
        }
        // 如果找到这个用户. 通过
        return new SimpleAuthenticationInfo(userDto, password, getName());
    }

}
