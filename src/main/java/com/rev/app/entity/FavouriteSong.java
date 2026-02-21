package com.rev.app.entity;

import java.time.LocalDateTime;

public class FavouriteSong {

    private int userId;
    private int songId;
    private LocalDateTime favoritedAt;

    public FavouriteSong() {}

    public FavouriteSong(int userId, int songId, LocalDateTime favoritedAt) {
        this.userId = userId;
        this.songId = songId;
        this.favoritedAt = favoritedAt;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getSongId() { return songId; }
    public void setSongId(int songId) { this.songId = songId; }

    public LocalDateTime getFavoritedAt() { return favoritedAt; }
    public void setFavoritedAt(LocalDateTime favoritedAt) { this.favoritedAt = favoritedAt; }
}

