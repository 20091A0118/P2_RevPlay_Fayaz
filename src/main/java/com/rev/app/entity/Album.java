package com.rev.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Album {

    private int albumId;
    private int artistId;
    private String title;
    // Genre removed from table definition provided by user, only in ARTIST_ACCOUNT
    // or SONG?
    // Wait, let me check schema provided.
    // Table ALBUM { ... title varchar, release_date date, description varchar,
    // cover_image_url varchar ... }
    // No genre column in ALBUM table in the new schema.
    private LocalDate releaseDate;
    private String description;
    private String coverImageUrl;
    private LocalDateTime createdAt;

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

    // Getters & Setters
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
}
