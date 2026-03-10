package com.revplay.app.repository;

import com.revplay.app.entity.FavoriteSong;
import com.revplay.app.entity.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFavoriteRepository extends JpaRepository<FavoriteSong, FavoriteId> {

    List<FavoriteSong> findByUserIdOrderByFavoritedAtDesc(int userId);

    Optional<FavoriteSong> findByUserIdAndSongId(int userId, int songId);

    long countByUserId(int userId);

    @Query("SELECT COUNT(f) FROM FavoriteSong f JOIN Song s ON f.songId = s.songId WHERE s.artistId = :artistId")
    long countFavoritesByArtistId(@Param("artistId") int artistId);

    @Query("SELECT f FROM FavoriteSong f JOIN Song s ON f.songId = s.songId WHERE s.artistId = :artistId ORDER BY f.favoritedAt DESC")
    List<FavoriteSong> findFavoritesByArtistId(@Param("artistId") int artistId);
}
