package com.revplay.app.entity;

import java.time.LocalDateTime;

public class ListeningHistory {
    private int historyId;
    private int userId;
    private int songId;
    private LocalDateTime playedAt;
    private String actionType;

    // Display fields
    private String songTitle;
    private String artistName;

    public ListeningHistory() {
    }

    public ListeningHistory(int historyId, int userId, int songId, LocalDateTime playedAt, String actionType) {
        this.historyId = historyId;
        this.userId = userId;
        this.songId = songId;
        this.playedAt = playedAt;
        this.actionType = actionType;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
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

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
