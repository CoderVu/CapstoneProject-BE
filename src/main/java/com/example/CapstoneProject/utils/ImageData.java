package com.example.CapstoneProject.utils;


import java.io.InputStream;

public class ImageData {
    private String name;
    private String lastModified;
    private String creationTime;
    private String contentType;
    private InputStream inputStream;

    public ImageData(String name, String lastModified, String creationTime, String contentType, InputStream inputStream) {
        this.name = name;
        this.lastModified = lastModified;
        this.creationTime = creationTime;
        this.contentType = contentType;
        this.inputStream = inputStream;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }}