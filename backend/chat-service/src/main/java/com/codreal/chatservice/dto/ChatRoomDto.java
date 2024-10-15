package com.codreal.chatservice.dto;

public class ChatRoomDto {
    private String roomId;
    private String roomName;

    public ChatRoomDto() {
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
