package com.revplay.app.service;

import com.revplay.app.entity.ArtistAccount;
import java.util.List;
import java.util.Map;

public interface IArtistService {
    Map<String, Object> registerArtist(ArtistAccount artist);

    Map<String, Object> loginArtist(String email, String password);

    ArtistAccount getArtistById(int id);

    List<ArtistAccount> getAllArtists();

    boolean updateProfile(ArtistAccount artist);

    Map<String, Object> getArtistStats(int artistId);

    List<ArtistAccount> searchArtists(String keyword);

    Map<String, Object> getSecurityQuestion(String email);

    Map<String, Object> forgotPassword(String email, String securityAnswer, String newPassword);
}
