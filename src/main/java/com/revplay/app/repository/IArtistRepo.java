package com.revplay.app.repository;

import com.revplay.app.entity.ArtistAccount;
import java.util.List;

public interface IArtistRepo {
    boolean registerArtist(ArtistAccount artist);

    ArtistAccount getArtistByEmail(String email);

    ArtistAccount getArtistById(int id);

    boolean updateArtistProfile(ArtistAccount artist);

    List<ArtistAccount> getAllArtists();

    List<ArtistAccount> searchArtists(String keyword);

    int getTotalPlays(int artistId);

    int getTotalFavorites(int artistId);
}
