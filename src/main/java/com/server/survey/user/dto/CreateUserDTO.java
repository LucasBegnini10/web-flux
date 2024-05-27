package com.server.survey.user.dto;

import lombok.Getter;

@Getter
public class CreateUserDTO {

    private String name;
    private String email;
    private String password;
}
