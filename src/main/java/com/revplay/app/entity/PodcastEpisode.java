package com.revplay.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PodcastEpisode {
    private int episodeId;
    private int podcastId;
    private String title;
    private int durationSeconds;
    private LocalDate releaseDate;
    private String fileUrl;
    private int playCount;
    private LocalDateTime createdAt;

    public PodcastEpisode() {
    }

    public PodcastEpisode(int episodeId, int podcastId, String title, int durationSeconds, LocalDate releaseDate,
                          String fileUrl, int playCount, LocalDateTime createdAt) {
        this.episodeId = episodeId;
        this.podcastId = podcastId;
        this.title = title;
        this.durationSeconds = durationSeconds;
        this.releaseDate = releaseDate;
        this.fileUrl = fileUrl;
        this.playCount = playCount;
        this.createdAt = createdAt;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
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

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

