package com.example.manager.factory;

import com.example.manager.dto.UserDto;
import com.example.manager.utils.AESUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Component
public class UserFactory {

    private static final Map<String, UserDto> users = new HashMap<>();

    private static final String[] USER_INFO = {"admin", "common"};
    private static final String[][] USER_SYSTEM_INFO = {
            {"opt", "/opt"},
            {"tomcat", "/opt/apache-tomcat-9.0.44/webapps"}
    };

    public static UserDto findUser(String username) {
        return users.get(username);
    }

    @PostConstruct
    public void createUsers() {
        for (String s : USER_INFO) {
            UserDto userDto = new UserDto(s, s,
                    new HashSet<>(Collections.singletonList("1")),
                    new HashSet<>(Collections.singletonList("*")),
                    AESUtils.getIV(),
                    AESUtils.getPassword(),
                    false,
                    s
            );
            users.put(userDto.getUsername(), userDto);
        }

        for (String[] array : USER_SYSTEM_INFO) {
            UserDto userDto = new UserDto(array[0], array[0],
                    new HashSet<>(Collections.singletonList("1")),
                    new HashSet<>(Collections.singletonList("*")),
                    AESUtils.getIV(),
                    AESUtils.getPassword(),
                    true,
                    array[1]
            );
            users.put(userDto.getUsername(), userDto);
        }
    }

    public static String getUserIv() {
        Subject subject = SecurityUtils.getSubject();
        UserDto userTempDto = (UserDto) subject.getPrincipal();
        return userTempDto.getIv();
    }

    public static String getUserKey() {
        Subject subject = SecurityUtils.getSubject();
        UserDto userTempDto = (UserDto) subject.getPrincipal();
        return userTempDto.getKey();
    }

    public static void main(String[] args) {
        try {
            InetAddress ip4 = Inet4Address.getLocalHost();
            System.out.println(ip4.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
