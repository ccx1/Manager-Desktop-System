package com.example.manager.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String username;

    private String password;

    private Set<String> permissions;

    private Set<String> roleNames;

    private String iv;

    private String key;

    private boolean systemPathAdmin;

    private String path;

}
