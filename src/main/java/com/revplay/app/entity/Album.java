package com.revplay.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Album {

    private int albumId;
    private int artistId;
    private String title;
    private LocalDate releaseDate;
    private String description;
    private String coverImageUrl;
    private LocalDateTime createdAt;
    private String artistName;

    public Album() {
    }

    public Album(int albumId, int artistId, String title,
                 LocalDate releaseDate, String description,
                 String coverImageUrl, LocalDateTime createdAt) {
        this.albumId = albumId;
        this.artistId = artistId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.createdAt = createdAt;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
