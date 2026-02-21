package com.rev.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Song {

    private int songId;
    private int artistId;
    private Integer albumId;
    private int genreId; // Changed from String genre
    private String title;
    private int durationSeconds;
    private LocalDate releaseDate;
    private String fileUrl;
    private int playCount;
    private String isActive;
    private LocalDateTime createdAt;

    // Helper field for display only (fetched via join)
    private String genreName;

    public Song() {
    }

    public Song(int songId, int artistId, Integer albumId,
                int genreId, String title, int durationSeconds,
                LocalDate releaseDate, String fileUrl,
                int playCount, String isActive,
                LocalDateTime createdAt) {
        this.songId = songId;
        this.artistId = artistId;
        this.albumId = albumId;
        this.genreId = genreId;
        this.title = title;
        this.durationSeconds = durationSeconds;
        this.releaseDate = releaseDate;
        this.fileUrl = fileUrl;
        this.playCount = playCount;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
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

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}

