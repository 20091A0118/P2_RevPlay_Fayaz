package com.revplay.app.service;

import com.revplay.app.entity.Podcast;
import com.revplay.app.entity.PodcastEpisode;
import com.revplay.app.repository.IPodcastRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PodcastServiceImpl implements IPodcastService {

    @Autowired
    private IPodcastRepo podcastRepo;

    @Override
    public boolean createPodcast(Podcast podcast) {
        return podcastRepo.createPodcast(podcast);
    }

    @Override
    public List<Podcast> getAllPodcasts() {
        return podcastRepo.getAllPodcasts();
    }

    @Override
    public Podcast getPodcastById(int id) {
        return podcastRepo.getPodcastById(id);
    }

    @Override
    public boolean addEpisode(PodcastEpisode episode) {
        return podcastRepo.addEpisode(episode);
    }

    @Override
    public List<PodcastEpisode> getEpisodesByPodcastId(int podcastId) {
        return podcastRepo.getEpisodesByPodcastId(podcastId);
    }

    @Override
    public boolean incrementEpisodePlayCount(int episodeId) {
        return podcastRepo.incrementEpisodePlayCount(episodeId);
    }
}
