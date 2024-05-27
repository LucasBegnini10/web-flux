package com.server.survey.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document
@Data
@ToString
public class User {

    public User(){}

    public User(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.password = builder.password;
        this.createdAt = builder.createdAt;
    }

    @Id
    private String id;

    private String name;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private RoleType role = RoleType.USER;
    private LocalDateTime createdAt;

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String email;
        private RoleType role = RoleType.USER;
        private String password;
        private LocalDateTime createdAt;

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withEmail(String email){
            this.email = email;
            return this;
        }

        public Builder withPassword(String password){
            this.password = password;
            return this;
        }

        public Builder withRole(RoleType role){
            this.role = role;
            return this;
        }

        public Builder withCreatedAt(){
            return this.withCreatedAt(LocalDateTime.now());
        }

        public Builder withCreatedAt(LocalDateTime createdAt){
            this.createdAt = createdAt;
            return this;
        }

        public User build(){
            return new User(this);
        }
    }
}
