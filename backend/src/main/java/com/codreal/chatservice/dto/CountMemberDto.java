package com.codreal.chatservice.dto;

public class CountMemberDto {

    private String roomId;

    private int countMember;
    private String type;

    public CountMemberDto() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getType() {
        return type;
    }

    public int getCountMember() {
        return countMember;
    }

    public void setCountMember(int countMember) {
        this.countMember = countMember;
    }

    public void setType(String type) {
        this.type = type;
    }

}
