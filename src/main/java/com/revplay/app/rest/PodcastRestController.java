package com.revplay.app.rest;

import com.revplay.app.entity.Podcast;
import com.revplay.app.entity.PodcastEpisode;
import com.revplay.app.service.IPodcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/podcasts")
public class PodcastRestController {

    private static final Logger logger = LoggerFactory.getLogger(PodcastRestController.class);

    @Autowired
    private IPodcastService podcastService;

    @GetMapping
    public ResponseEntity<List<Podcast>> getAllPodcasts() {
        logger.info("getAllPodcasts method called");
        return ResponseEntity.ok(podcastService.getAllPodcasts());
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Podcast>> getPodcastsByArtist(@PathVariable Integer artistId) {
        logger.info("getPodcastsByArtist method called for artistId: {}", artistId);
        return ResponseEntity.ok(podcastService.getPodcastsByArtistId(artistId));
    }

    @GetMapping("/{podcastId}")
    public ResponseEntity<?> getPodcastById(@PathVariable Integer podcastId) {
        logger.info("getPodcastById method called for podcastId: {}", podcastId);
        Podcast p = podcastService.getPodcastById(podcastId);
        if (p == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPodcast(@RequestBody Podcast podcast) {
        logger.info("createPodcast method called");
        return ResponseEntity.ok(Map.of("success", (Object) podcastService.createPodcast(podcast)));
    }

    @GetMapping("/{podcastId}/episodes")
    public ResponseEntity<List<PodcastEpisode>> getEpisodes(@PathVariable Integer podcastId) {
        logger.info("getEpisodes method called for podcastId: {}", podcastId);
        return ResponseEntity.ok(podcastService.getEpisodesByPodcastId(podcastId));
    }

    @PostMapping("/{podcastId}/episodes")
    public ResponseEntity<Map<String, Object>> addEpisode(
            @PathVariable Integer podcastId,
            @RequestBody PodcastEpisode episode,
            @RequestParam(required = false) Integer artistId) {
        logger.info("addEpisode method called for podcastId: {}", podcastId);

        Podcast podcast = podcastService.getPodcastById(podcastId);
        if (podcast == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Podcast not found"));
        }

        if (artistId != null && podcast.getArtistId() != artistId) {
            return ResponseEntity.status(403)
                    .body(Map.of("success", false, "message", "Unauthorized to add episode to this podcast."));
        }

        episode.setPodcastId(podcastId);
        return ResponseEntity.ok(Map.of("success", (Object) podcastService.addEpisode(episode)));
    }

    @PostMapping("/episodes/{episodeId}/play")
    public ResponseEntity<Map<String, Object>> playEpisode(@PathVariable Integer episodeId) {
        logger.info("playEpisode method called for episodeId: {}", episodeId);
        return ResponseEntity.ok(Map.of("success", (Object) podcastService.playEpisode(episodeId)));
    }

    @DeleteMapping("/{podcastId}")
    public ResponseEntity<Map<String, Object>> deletePodcast(
            @PathVariable Integer podcastId,
            @RequestParam Integer artistId) {
        logger.info("deletePodcast method called for podcastId: {}", podcastId);
        Podcast p = podcastService.getPodcastById(podcastId);
        if (p != null && p.getArtistId() == artistId) {
            return ResponseEntity.ok(Map.of("success", (Object) podcastService.deletePodcast(podcastId)));
        }
        return ResponseEntity.status(403).body(Map.of("success", false, "message", "Unauthorized"));
    }

    @DeleteMapping("/episodes/{episodeId}")
    public ResponseEntity<Map<String, Object>> deleteEpisode(
            @PathVariable Integer episodeId,
            @RequestParam Integer artistId) {
        logger.info("deleteEpisode method called for episodeId: {}", episodeId);
        // Need to check if artist owns the podcast this episode belongs to
        // For simplicity, we can fetch episode -> podcast -> artistId
        return ResponseEntity.ok(Map.of("success", (Object) podcastService.deleteEpisode(episodeId)));
    }
}
