package com.server.survey.user;

import lombok.Getter;

@Getter
public enum RoleType {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER");

    private final String role;

    RoleType(String role){
        this.role = role;
    }

    public String getRoleWithoutPrefix(){
        return role.substring(5);
    }

}
