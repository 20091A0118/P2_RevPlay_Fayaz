package com.rev.app.entity;

import java.time.LocalDateTime;

public class ArtistAccount {

    private int artistId;
    private String stageName;
    private String email;
    private String passwordHash;
    private String bio;
    private String genre;
    private String instagramLink;
    private String youtubeLink;
    private String spotifyLink;
    private String securityQuestion;
    private String securityAnswerHash;
    private String passwordHint;
    private String status;
    private LocalDateTime createdAt;

    public ArtistAccount() {
    }

    public ArtistAccount(int artistId, String stageName, String email,
                         String passwordHash, String bio, String genre,
                         String instagramLink, String youtubeLink,
                         String spotifyLink, String status,
                         LocalDateTime createdAt) {
        this.artistId = artistId;
        this.stageName = stageName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.bio = bio;
        this.genre = genre;
        this.instagramLink = instagramLink;
        this.youtubeLink = youtubeLink;
        this.spotifyLink = spotifyLink;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String getSpotifyLink() {
        return spotifyLink;
    }

    public void setSpotifyLink(String spotifyLink) {
        this.spotifyLink = spotifyLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswerHash() {
        return securityAnswerHash;
    }

    public void setSecurityAnswerHash(String securityAnswerHash) {
        this.securityAnswerHash = securityAnswerHash;
    }

    public String getPasswordHint() {
        return passwordHint;
    }

    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
    }
}
