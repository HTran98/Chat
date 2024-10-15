package com.codreal.chatservice.dto;

import org.springframework.data.annotation.Id;

public class UserDto {

    @Id
    private String id;

    private String username;


    public UserDto() {
    }

    public UserDto(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
