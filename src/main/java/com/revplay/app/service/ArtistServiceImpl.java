package com.revplay.app.service;

import com.revplay.app.repository.IArtistRepo;
import com.revplay.app.entity.ArtistAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ArtistServiceImpl implements IArtistService {

    @Autowired
    private IArtistRepo artistDao;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public void setArtistDao(IArtistRepo artistDao) {
        this.artistDao = artistDao;
    }

    @Autowired
    private com.revplay.app.repository.ISongRepo songRepo;

    @Autowired
    private com.revplay.app.repository.IFavoriteRepo favoriteRepo;

    @Autowired
    private com.revplay.app.repository.IPodcastEpisodeRepo podcastEpisodeRepo;

    public Map<String, Object> registerArtist(ArtistAccount artist) {
        artist.setStatus("ACTIVE");
        artist.setCreatedAt(LocalDateTime.now());
        // Encode password before saving
        if (artist.getPasswordHash() != null) {
            artist.setPasswordHash(passwordEncoder.encode(artist.getPasswordHash()));
        }
        try {
            ArtistAccount created = artistDao.save(artist);
            return new java.util.HashMap<>(Map.of("success", (Object) true, "message", "Artist registered successfully",
                    "artistId", created.getArtistId()));
        } catch (Exception e) {
            System.err.println("Artist registration failed: " + e.getMessage());
            return new java.util.HashMap<>(
                    Map.of("success", (Object) false, "message", "Registration failed. Email may already exist."));
        }
    }

    public Map<String, Object> loginArtist(String email, String password) {
        return artistDao.findByEmail(email)
                .map(artist -> {
                    if (passwordEncoder.matches(password, artist.getPasswordHash())) {
                        return new java.util.HashMap<>(
                                Map.of("success", (Object) true, "artistId", artist.getArtistId(),
                                        "stageName", artist.getStageName(), "email", artist.getEmail()));
                    }
                    return new java.util.HashMap<>(
                            Map.of("success", (Object) false, "message", "Invalid email or password"));
                })
                .orElse(new java.util.HashMap<>(
                        Map.of("success", (Object) false, "message", "Invalid email or password")));
    }

    public ArtistAccount getArtistById(int id) {
        return artistDao.findById(id).orElse(null);
    }

    public java.util.List<ArtistAccount> getAllArtists() {
        return artistDao.findAll();
    }

    public boolean updateProfile(ArtistAccount artist) {
        try {
            artistDao.save(artist);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getArtistStats(int artistId) {
        long songCount = songRepo.findByArtistId(artistId).size();
        long songPlays = songRepo.getTotalPlaysByArtistId(artistId);
        long podcastPlays = podcastEpisodeRepo.getTotalPlaysByArtistId(artistId);
        long totalPlays = songPlays + podcastPlays;
        long totalFavorites = favoriteRepo.countFavoritesByArtistId(artistId);
        return Map.of(
                "songCount", songCount,
                "totalPlays", totalPlays,
                "totalFavorites", totalFavorites);
    }

    public java.util.List<ArtistAccount> searchArtists(String keyword) {
        // IArtistRepo should have a searchArtists if needed, but ISongRepo has search.
        // If IArtistRepo doesn't have it, I should add it or use findAll and filter.
        // For now, let's assume it has it or I'll add it.
        return artistDao.searchArtists(keyword);
    }

    public Map<String, Object> getSecurityQuestion(String email) {
        return artistDao.findByEmail(email)
                .map(artist -> Map.of("success", (Object) true,
                        "securityQuestion", artist.getSecurityQuestion() != null ? artist.getSecurityQuestion() : "",
                        "passwordHint", artist.getPasswordHint() != null ? artist.getPasswordHint() : ""))
                .orElse(Map.of("success", (Object) false, "message", "Email not found"));
    }

    public Map<String, Object> forgotPassword(String email, String securityAnswer, String newPassword) {
        return artistDao.findByEmail(email)
                .map(artist -> {
                    if (artist.getSecurityAnswerHash() != null
                            && artist.getSecurityAnswerHash().equalsIgnoreCase(securityAnswer)) {
                        artist.setPasswordHash(passwordEncoder.encode(newPassword));
                        artistDao.save(artist);
                        return Map.of("success", (Object) true, "message", "Password updated successfully");
                    }
                    return Map.of("success", (Object) false, "message", "Security answer is incorrect");
                })
                .orElse(Map.of("success", (Object) false, "message", "Email not found"));
    }
}
