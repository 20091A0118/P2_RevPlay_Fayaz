package com.revplay.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "FAVORITE_SONG")
@IdClass(FavoriteId.class)
public class FavoriteSong {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "song_id")
    private int songId;

    @Column(name = "favorited_at")
    private LocalDateTime favoritedAt;

    public FavoriteSong() {
    }

    public FavoriteSong(int userId, int songId, LocalDateTime favoritedAt) {
        this.userId = userId;
        this.songId = songId;
        this.favoritedAt = favoritedAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public LocalDateTime getFavoritedAt() {
        return favoritedAt;
    }

    public void setFavoritedAt(LocalDateTime favoritedAt) {
        this.favoritedAt = favoritedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (favoritedAt == null) {
            favoritedAt = LocalDateTime.now();
        }
    }
}
