package com.revplay.app.repository;

import com.revplay.app.entity.PodcastEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPodcastEpisodeRepo extends JpaRepository<PodcastEpisode, Integer> {
    List<PodcastEpisode> findByPodcastIdOrderByReleaseDateDesc(int podcastId);

    @Query("SELECT COALESCE(SUM(e.playCount), 0) FROM PodcastEpisode e, Podcast p WHERE e.podcastId = p.podcastId AND p.artistId = :artistId")
    long getTotalPlaysByArtistId(@Param("artistId") int artistId);
}
