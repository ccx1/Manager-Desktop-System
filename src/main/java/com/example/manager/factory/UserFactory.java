package com.example.manager.factory;

import com.example.manager.dto.UserDto;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class UserFactory {

    private static final Map<String, UserDto> users = new HashMap<>();

    public static UserDto findUser(String username) {
        return users.get(username);
    }

    @PostConstruct
    public void createUsers() {
        UserDto userDto = new UserDto("admin", "admin",
                new HashSet<>(Collections.singletonList("1")),
                new HashSet<>(Collections.singletonList("*"))
        );
        users.put(userDto.getUsername(), userDto);
    }

}
