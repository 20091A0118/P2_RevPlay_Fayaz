package com.revplay.app.service;

import com.revplay.app.repository.IPodcastRepo;
import com.revplay.app.repository.IPodcastEpisodeRepo;
import com.revplay.app.entity.Podcast;
import com.revplay.app.entity.PodcastEpisode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PodcastServiceImpl implements IPodcastService {

    @Autowired
    private IPodcastRepo podcastRepo;

    @Autowired
    private IPodcastEpisodeRepo episodeRepo;

    public void setPodcastRepo(IPodcastRepo podcastRepo) {
        this.podcastRepo = podcastRepo;
    }

    public void setEpisodeRepo(IPodcastEpisodeRepo episodeRepo) {
        this.episodeRepo = episodeRepo;
    }

    public boolean createPodcast(Podcast podcast) {
        try {
            System.out.println("Creating podcast: " + podcast.getTitle() + " for artist: " + podcast.getArtistId());
            return podcastRepo.save(podcast) != null;
        } catch (Exception e) {
            System.err.println("Error creating podcast: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Podcast> getAllPodcasts() {
        try {
            List<Podcast> podcasts = podcastRepo.findByOrderByCreatedAtDesc();
            System.out.println("DEBUG: getAllPodcasts found " + podcasts.size() + " podcasts.");
            return podcasts;
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR fetching all podcasts: " + e.getMessage());
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public List<Podcast> getPodcastsByArtistId(Integer artistId) {
        try {
            return podcastRepo.findByArtistIdOrderByCreatedAtDesc(artistId);
        } catch (Exception e) {
            System.err.println("Error fetching podcasts for artist " + artistId + ": " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public Podcast getPodcastById(int id) {
        try {
            return podcastRepo.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error fetching podcast " + id + ": " + e.getMessage());
            return null;
        }
    }

    public boolean addEpisode(PodcastEpisode episode) {
        try {
            if (!podcastRepo.existsById(episode.getPodcastId())) {
                System.err.println("Cannot add episode: Podcast " + episode.getPodcastId() + " not found");
                return false;
            }
            episode.setCreatedAt(java.time.LocalDateTime.now());
            return episodeRepo.save(episode) != null;
        } catch (Exception e) {
            System.err.println("Error adding episode: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<PodcastEpisode> getEpisodesByPodcastId(int podcastId) {
        return episodeRepo.findByPodcastIdOrderByReleaseDateDesc(podcastId);
    }

    public boolean playEpisode(int episodeId) {
        return episodeRepo.findById(episodeId).map(episode -> {
            episode.setPlayCount(episode.getPlayCount() + 1);
            episodeRepo.save(episode);
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean deletePodcast(int podcastId) {
        try {
            // Delete episodes first if not cascading
            List<PodcastEpisode> episodes = episodeRepo.findByPodcastIdOrderByReleaseDateDesc(podcastId);
            episodeRepo.deleteAll(episodes);
            podcastRepo.deleteById(podcastId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteEpisode(int episodeId) {
        try {
            episodeRepo.deleteById(episodeId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
