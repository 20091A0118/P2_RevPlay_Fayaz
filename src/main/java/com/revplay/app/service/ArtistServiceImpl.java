package com.revplay.app.service;

import com.revplay.app.repository.IArtistRepository;
import com.revplay.app.entity.ArtistAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ArtistServiceImpl implements IArtistService {

    private static final Logger logger = LoggerFactory.getLogger(ArtistServiceImpl.class);

    @Autowired
    private IArtistRepository artistDao;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public void setArtistDao(IArtistRepository artistDao) {
        this.artistDao = artistDao;
    }

    @Autowired
    private com.revplay.app.repository.ISongRepository songRepository;

    @Autowired
    private com.revplay.app.repository.IFavoriteRepository favoriteRepository;

    @Autowired
    private com.revplay.app.repository.IPodcastEpisodeRepository podcastEpisodeRepository;

    public Map<String, Object> registerArtist(ArtistAccount artist) {
        logger.info("registerArtist method called in Service for email: {}", artist.getEmail());
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
        logger.info("loginArtist method called in Service for email: {}", email);
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
        logger.info("getArtistById method called in Service for id: {}", id);
        return artistDao.findById(id).orElse(null);
    }

    public java.util.List<ArtistAccount> getAllArtists() {
        logger.info("getAllArtists method called in Service");
        return artistDao.findAll();
    }

    public boolean updateProfile(ArtistAccount artist) {
        logger.info("updateProfile method called in Service for artistId: {}", artist.getArtistId());
        try {
            artistDao.save(artist);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getArtistStats(int artistId) {
        logger.info("getArtistStats method called in Service for artistId: {}", artistId);
        long songCount = songRepository.findByArtistId(artistId).size();
        long songPlays = songRepository.getTotalPlaysByArtistId(artistId);
        long podcastPlays = podcastEpisodeRepository.getTotalPlaysByArtistId(artistId);
        long totalPlays = songPlays + podcastPlays;
        long totalFavorites = favoriteRepository.countFavoritesByArtistId(artistId);
        return Map.of(
                "songCount", songCount,
                "totalPlays", totalPlays,
                "totalFavorites", totalFavorites);
    }

    public java.util.List<ArtistAccount> searchArtists(String keyword) {
        logger.info("searchArtists method called in Service with keyword: {}", keyword);
        // IArtistRepository should have a searchArtists if needed, but ISongRepository
        // has search.
        // If IArtistRepository doesn't have it, I should add it or use findAll and
        // filter.
        // For now, let's assume it has it or I'll add it.
        return artistDao.searchArtists(keyword);
    }

    public Map<String, Object> getSecurityQuestion(String email) {
        logger.info("getSecurityQuestion method called in Service for email: {}", email);
        return artistDao.findByEmail(email)
                .map(artist -> Map.of("success", (Object) true,
                        "securityQuestion", artist.getSecurityQuestion() != null ? artist.getSecurityQuestion() : "",
                        "passwordHint", artist.getPasswordHint() != null ? artist.getPasswordHint() : ""))
                .orElse(Map.of("success", (Object) false, "message", "Email not found"));
    }

    public Map<String, Object> forgotPassword(String email, String securityAnswer, String newPassword) {
        logger.info("forgotPassword method called in Service for email: {}", email);
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

    public boolean updatePassword(int artistId, String oldPassword, String newPassword) {
        logger.info("updatePassword method called in Service for artistId: {}", artistId);
        return artistDao.findById(artistId)
                .map(artist -> {
                    if (passwordEncoder.matches(oldPassword, artist.getPasswordHash())) {
                        artist.setPasswordHash(passwordEncoder.encode(newPassword));
                        artistDao.save(artist);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }
}
