package com.revplay.app.repository;

import com.revplay.app.entity.Podcast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPodcastRepo extends JpaRepository<Podcast, Integer> {
    List<Podcast> findByOrderByCreatedAtDesc();

    List<Podcast> findByArtistIdOrderByCreatedAtDesc(Integer artistId);
}
