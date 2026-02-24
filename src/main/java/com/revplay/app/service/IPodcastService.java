package com.revplay.app.service;

import com.revplay.app.entity.Podcast;
import com.revplay.app.entity.PodcastEpisode;

import java.util.List;

public interface IPodcastService {
    boolean createPodcast(Podcast podcast);

    List<Podcast> getAllPodcasts();

    Podcast getPodcastById(int id);

    boolean addEpisode(PodcastEpisode episode);

    List<PodcastEpisode> getEpisodesByPodcastId(int podcastId);

    boolean incrementEpisodePlayCount(int episodeId);
}
