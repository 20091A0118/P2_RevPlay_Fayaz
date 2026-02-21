package com.rev.app.entity;

import java.time.LocalDateTime;

public class Playlist {

    private int playlistId;
    private int userId;
    private String name;
    private String description;
    private String privacyStatus; // Changed from boolean isPublic
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Playlist() {
    }

    public Playlist(int playlistId, int userId, String name,
                    String description, String privacyStatus,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.playlistId = playlistId;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.privacyStatus = privacyStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrivacyStatus() {
        return privacyStatus;
    }

    public void setPrivacyStatus(String privacyStatus) {
        this.privacyStatus = privacyStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

