package com.studentapp.backend.dto;

import lombok.Data;

public class SummaryRequest {
    private String text;
    private SourceType sourceType; // TEXT or YOUTUBE
    private String videoUrl;

    public enum SourceType {
        TEXT, YOUTUBE
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
