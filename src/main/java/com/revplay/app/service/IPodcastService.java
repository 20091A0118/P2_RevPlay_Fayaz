package com.revplay.app.service;

import com.revplay.app.entity.Podcast;
import com.revplay.app.entity.PodcastEpisode;
import java.util.List;

public interface IPodcastService {
    boolean createPodcast(Podcast podcast);

    List<Podcast> getAllPodcasts();

    List<Podcast> getPodcastsByArtistId(Integer artistId);

    Podcast getPodcastById(int id);

    boolean addEpisode(PodcastEpisode episode);

    List<PodcastEpisode> getEpisodesByPodcastId(int podcastId);

    boolean playEpisode(int episodeId);

    boolean deletePodcast(int podcastId);

    boolean deleteEpisode(int episodeId);
}
