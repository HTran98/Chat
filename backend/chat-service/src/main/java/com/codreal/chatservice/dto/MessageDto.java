package com.codreal.chatservice.dto;

public class MessageDto {
    private String id;
    private String sender;
    private String senderName;
    private String content;
    private String roomId;
    private long timestamp;
    private String fileName;
    private String urlDowload;
    // Constructor
    public MessageDto() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUrlDowload() {
        return urlDowload;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setUrlDowload(String urlDowload) {
        this.urlDowload = urlDowload;
    }

}
