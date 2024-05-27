package com.server.survey.auth.dto;

import lombok.Getter;

@Getter
public class AuthResponseDTO {
    private String token;
    private Boolean success = true;

    public AuthResponseDTO(String token) {
        this.token = token;
    }
}
