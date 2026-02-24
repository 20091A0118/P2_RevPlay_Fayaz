package com.revplay.app.controller;

import com.revplay.app.entity.Podcast;
import com.revplay.app.entity.PodcastEpisode;
import com.revplay.app.service.IPodcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/podcasts")
public class PodcastController {

    @Autowired
    private IPodcastService podcastService;

    // CREATE - Create new podcast
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPodcast(@RequestBody Podcast podcast) {
        boolean success = podcastService.createPodcast(podcast);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Podcast created successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to create podcast", "success", false));
    }

    // READ - Get all podcasts
    @GetMapping
    public ResponseEntity<List<Podcast>> getAllPodcasts() {
        return ResponseEntity.ok(podcastService.getAllPodcasts());
    }

    // READ - Get podcast by ID
    @GetMapping("/{id}")
    public ResponseEntity<Podcast> getPodcastById(@PathVariable int id) {
        Podcast podcast = podcastService.getPodcastById(id);
        if (podcast != null) {
            return ResponseEntity.ok(podcast);
        }
        return ResponseEntity.notFound().build();
    }

    // CREATE - Add episode to podcast
    @PostMapping("/{id}/episodes")
    public ResponseEntity<Map<String, Object>> addEpisode(@PathVariable int id, @RequestBody PodcastEpisode episode) {
        episode.setPodcastId(id);
        boolean success = podcastService.addEpisode(episode);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Episode added successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to add episode", "success", false));
    }

    // READ - Get episodes by podcast ID
    @GetMapping("/{id}/episodes")
    public ResponseEntity<List<PodcastEpisode>> getEpisodes(@PathVariable int id) {
        return ResponseEntity.ok(podcastService.getEpisodesByPodcastId(id));
    }

    // UPDATE - Increment episode play count
    @PutMapping("/episodes/{episodeId}/play")
    public ResponseEntity<Map<String, Object>> incrementPlayCount(@PathVariable int episodeId) {
        boolean success = podcastService.incrementEpisodePlayCount(episodeId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Episode play count incremented", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to increment play count", "success", false));
    }
}
