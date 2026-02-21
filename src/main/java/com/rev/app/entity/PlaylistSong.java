package com.rev.app.entity;

import java.time.LocalDateTime;

public class PlaylistSong {

    private int playlistId;
    private int songId;
    private LocalDateTime addedAt;

    public PlaylistSong() {}

    public PlaylistSong(int playlistId, int songId, LocalDateTime addedAt) {
        this.playlistId = playlistId;
        this.songId = songId;
        this.addedAt = addedAt;
    }

    public int getPlaylistId() { return playlistId; }
    public void setPlaylistId(int playlistId) { this.playlistId = playlistId; }

    public int getSongId() { return songId; }
    public void setSongId(int songId) { this.songId = songId; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}