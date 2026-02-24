package com.revplay.app.service;

import com.revplay.app.entity.ArtistAccount;

import java.util.List;

public interface IArtistService {
    boolean registerArtist(ArtistAccount artist);

    ArtistAccount getArtistById(int id);

    ArtistAccount getArtistByEmail(String email);

    List<ArtistAccount> getAllArtists();

    boolean updateArtist(ArtistAccount artist);

    List<ArtistAccount> searchArtists(String keyword);

    int getTotalPlays(int artistId);

    int getTotalFavorites(int artistId);
}
