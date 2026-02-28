package com.revplay.app.repository;

import com.revplay.app.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISongRepo extends JpaRepository<Song, Integer> {

    List<Song> findByArtistId(int artistId);

    List<Song> findByAlbumId(int albumId);

    List<Song> findByGenreId(int genreId);

    @Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Song> searchSongs(@Param("keyword") String keyword);

    List<Song> findTop10ByOrderByPlayCountDesc();

    @Query("SELECT COALESCE(SUM(s.playCount), 0) FROM Song s WHERE s.artistId = :artistId")
    Long getTotalPlaysByArtistId(@Param("artistId") int artistId);
}
