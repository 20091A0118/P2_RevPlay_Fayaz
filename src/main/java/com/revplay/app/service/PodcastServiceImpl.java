package com.revplay.app.service;

import com.revplay.app.repository.IPodcastRepository;
import com.revplay.app.repository.IPodcastEpisodeRepository;
import com.revplay.app.entity.Podcast;
import com.revplay.app.entity.PodcastEpisode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PodcastServiceImpl implements IPodcastService {

    private static final Logger logger = LoggerFactory.getLogger(PodcastServiceImpl.class);

    @Autowired
    private IPodcastRepository podcastRepository;

    @Autowired
    private IPodcastEpisodeRepository episodeRepository;

    public void setPodcastRepository(IPodcastRepository podcastRepository) {
        this.podcastRepository = podcastRepository;
    }

    public void setEpisodeRepository(IPodcastEpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    public boolean createPodcast(Podcast podcast) {
        logger.info("createPodcast method called in Service");
        try {
            System.out.println("Creating podcast: " + podcast.getTitle() + " for artist: " + podcast.getArtistId());
            return podcastRepository.save(podcast) != null;
        } catch (Exception e) {
            System.err.println("Error creating podcast: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Podcast> getAllPodcasts() {
        logger.info("getAllPodcasts method called in Service");
        try {
            List<Podcast> podcasts = podcastRepository.findByOrderByCreatedAtDesc();
            System.out.println("DEBUG: getAllPodcasts found " + podcasts.size() + " podcasts.");
            return podcasts;
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR fetching all podcasts: " + e.getMessage());
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public List<Podcast> getPodcastsByArtistId(Integer artistId) {
        logger.info("getPodcastsByArtistId method called in Service for artistId: {}", artistId);
        try {
            return podcastRepository.findByArtistIdOrderByCreatedAtDesc(artistId);
        } catch (Exception e) {
            System.err.println("Error fetching podcasts for artist " + artistId + ": " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public Podcast getPodcastById(int id) {
        logger.info("getPodcastById method called in Service for id: {}", id);
        try {
            return podcastRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error fetching podcast " + id + ": " + e.getMessage());
            return null;
        }
    }

    public boolean addEpisode(PodcastEpisode episode) {
        logger.info("addEpisode method called in Service");
        try {
            if (!podcastRepository.existsById(episode.getPodcastId())) {
                System.err.println("Cannot add episode: Podcast " + episode.getPodcastId() + " not found");
                return false;
            }
            episode.setCreatedAt(java.time.LocalDateTime.now());
            return episodeRepository.save(episode) != null;
        } catch (Exception e) {
            System.err.println("Error adding episode: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<PodcastEpisode> getEpisodesByPodcastId(int podcastId) {
        logger.info("getEpisodesByPodcastId method called in Service for podcastId: {}", podcastId);
        return episodeRepository.findByPodcastIdOrderByReleaseDateDesc(podcastId);
    }

    public boolean playEpisode(int episodeId) {
        logger.info("playEpisode method called in Service for episodeId: {}", episodeId);
        return episodeRepository.findById(episodeId).map(episode -> {
            episode.setPlayCount(episode.getPlayCount() + 1);
            episodeRepository.save(episode);
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean deletePodcast(int podcastId) {
        logger.info("deletePodcast method called in Service for podcastId: {}", podcastId);
        try {
            // Delete episodes first if not cascading
            List<PodcastEpisode> episodes = episodeRepository.findByPodcastIdOrderByReleaseDateDesc(podcastId);
            episodeRepository.deleteAll(episodes);
            podcastRepository.deleteById(podcastId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteEpisode(int episodeId) {
        logger.info("deleteEpisode method called in Service for episodeId: {}", episodeId);
        try {
            episodeRepository.deleteById(episodeId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
