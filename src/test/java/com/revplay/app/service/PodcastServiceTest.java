package com.revplay.app.service;

import com.revplay.app.entity.Podcast;
import com.revplay.app.entity.PodcastEpisode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for PodcastServiceImpl using mock DAO.
 */
public class PodcastServiceTest {

    private MockPodcastDao mockPodcastDao;
    private MockPodcastEpisodeDao mockEpisodeDao;
    private PodcastServiceImpl podcastService;

    @BeforeEach
    public void setup() {
        mockPodcastDao = new MockPodcastDao();
        mockEpisodeDao = new MockPodcastEpisodeDao();
        podcastService = new PodcastServiceImpl();
        podcastService.setPodcastRepo(mockPodcastDao);
        podcastService.setEpisodeRepo(mockEpisodeDao);
    }

    @Test
    public void testCreatePodcast_Success() {
        Podcast podcast = new Podcast();
        podcast.setTitle("Tech Talk");
        podcast.setHostName("John Doe");
        podcast.setCategory("Technology");

        boolean result = podcastService.createPodcast(podcast);

        assertTrue(result, "Podcast creation should succeed");
        assertEquals(1, podcast.getPodcastId());
    }

    @Test
    public void testAddEpisode_Success() {
        Podcast podcast = new Podcast();
        podcast.setTitle("Episode Test Podcast");
        mockPodcastDao.createPodcast(podcast);

        PodcastEpisode episode = new PodcastEpisode();
        episode.setPodcastId(podcast.getPodcastId());
        episode.setTitle("Episode 1");
        episode.setDurationSeconds(3600);

        boolean result = podcastService.addEpisode(episode);

        assertTrue(result, "Adding episode should succeed");
        assertEquals(1, episode.getEpisodeId());
    }

    @Test
    public void testAddEpisode_InvalidPodcast() {
        PodcastEpisode episode = new PodcastEpisode();
        episode.setPodcastId(999);
        episode.setTitle("Orphan Episode");

        boolean result = podcastService.addEpisode(episode);

        assertFalse(result, "Adding episode to non-existent podcast should fail");
    }

    @Test
    public void testPlayEpisode_Success() {
        Podcast podcast = new Podcast();
        podcast.setTitle("Play Test Podcast");
        mockPodcastDao.save(podcast);

        PodcastEpisode episode = new PodcastEpisode();
        episode.setPodcastId(podcast.getPodcastId());
        episode.setTitle("Play Me");
        mockEpisodeDao.save(episode);

        boolean result = podcastService.playEpisode(episode.getEpisodeId());

        assertTrue(result);
        assertEquals(1, mockEpisodeDao.findById(episode.getEpisodeId()).get().getPlayCount());
    }

    @Test
    public void testPlayEpisode_NotFound() {
        boolean result = podcastService.playEpisode(99999);

        assertFalse(result, "Playing non-existent episode should fail");
    }

    @Test
    public void testGetAllPodcasts() {
        mockPodcastDao.createPodcast(new Podcast());
        mockPodcastDao.createPodcast(new Podcast());

        List<Podcast> all = podcastService.getAllPodcasts();

        assertNotNull(all);
        assertEquals(2, all.size());
    }
}
