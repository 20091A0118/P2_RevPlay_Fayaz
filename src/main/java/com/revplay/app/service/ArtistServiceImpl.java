package com.revplay.app.service;

import com.revplay.app.entity.ArtistAccount;
import com.revplay.app.repository.IArtistRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtistServiceImpl implements IArtistService {

    @Autowired
    private IArtistRepo artistRepo;

    @Override
    public boolean registerArtist(ArtistAccount artist) {
        if (artist.getCreatedAt() == null) {
            artist.setCreatedAt(LocalDateTime.now());
        }
        if (artist.getStatus() == null || artist.getStatus().isEmpty()) {
            artist.setStatus("ACTIVE");
        }
        return artistRepo.registerArtist(artist);
    }

    @Override
    public ArtistAccount getArtistById(int id) {
        return artistRepo.getArtistById(id);
    }

    @Override
    public ArtistAccount getArtistByEmail(String email) {
        return artistRepo.getArtistByEmail(email);
    }

    @Override
    public List<ArtistAccount> getAllArtists() {
        return artistRepo.getAllArtists();
    }

    @Override
    public boolean updateArtist(ArtistAccount artist) {
        return artistRepo.updateArtistProfile(artist);
    }

    @Override
    public List<ArtistAccount> searchArtists(String keyword) {
        return artistRepo.searchArtists(keyword);
    }

    @Override
    public int getTotalPlays(int artistId) {
        return artistRepo.getTotalPlays(artistId);
    }

    @Override
    public int getTotalFavorites(int artistId) {
        return artistRepo.getTotalFavorites(artistId);
    }
}
