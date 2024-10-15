package com.codreal.chatservice.dto;

public class FileResponseDto {
    private String fileName;
    private String fileUrl;

    public FileResponseDto() {
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

}
