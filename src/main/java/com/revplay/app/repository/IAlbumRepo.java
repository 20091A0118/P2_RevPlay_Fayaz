package com.revplay.app.repository;

import com.revplay.app.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAlbumRepo extends JpaRepository<Album, Integer> {

    List<Album> findByArtistId(int artistId);

    @Query("SELECT a FROM Album a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Album> searchAlbums(@Param("keyword") String keyword);
}
