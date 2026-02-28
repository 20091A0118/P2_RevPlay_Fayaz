package com.revplay.app.repository;

import com.revplay.app.entity.ArtistAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IArtistRepo extends JpaRepository<ArtistAccount, Integer> {

    Optional<ArtistAccount> findByEmail(String email);

    Optional<ArtistAccount> findByStageName(String stageName);

    @Query("SELECT a FROM ArtistAccount a WHERE LOWER(a.stageName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.genre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ArtistAccount> searchArtists(@Param("keyword") String keyword);

    @Query("SELECT COALESCE(SUM(s.playCount), 0) FROM Song s WHERE s.artistId = :artistId")
    int getTotalPlays(@Param("artistId") int artistId);

    @Query("SELECT COUNT(fs) FROM FavoriteSong fs JOIN Song s ON fs.songId = s.songId WHERE s.artistId = :artistId")
    int getTotalFavorites(@Param("artistId") int artistId);
}
