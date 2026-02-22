package com.revplay.app.entity;

import java.time.LocalDateTime;

public class Podcast {
    private int podcastId;
    private String title;
    private String hostName;
    private String category;
    private String description;
    private String coverImageUrl;
    private LocalDateTime createdAt;

    public Podcast() {
    }

    public Podcast(int podcastId, String title, String hostName, String category, String description,
                   LocalDateTime createdAt) {
        this.podcastId = podcastId;
        this.title = title;
        this.hostName = hostName;
        this.category = category;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(int podcastId) {
        this.podcastId = podcastId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
}
