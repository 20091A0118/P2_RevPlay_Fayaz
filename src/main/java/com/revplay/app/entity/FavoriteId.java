package com.revplay.app.entity;

import java.io.Serializable;
import java.util.Objects;

public class FavoriteId implements Serializable {
    private int userId;
    private int songId;

    public FavoriteId() {
    }

    public FavoriteId(int userId, int songId) {
        this.userId = userId;
        this.songId = songId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FavoriteId that = (FavoriteId) o;
        return userId == that.userId && songId == that.songId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, songId);
    }
}
