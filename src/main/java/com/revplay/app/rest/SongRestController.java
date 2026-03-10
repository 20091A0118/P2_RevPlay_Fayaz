package com.revplay.app.rest;

import com.revplay.app.entity.Song;
import com.revplay.app.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/songs")
public class SongRestController {

    private static final Logger logger = LoggerFactory.getLogger(SongRestController.class);

    @Autowired
    private ISongService songService;

    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        logger.info("getAllSongs method called");
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @GetMapping("/{songId}")
    public ResponseEntity<?> getSongById(@PathVariable int songId) {
        logger.info("getSongById method called for songId: {}", songId);
        Song song = songService.getSongById(songId);
        if (song == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(song);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadSong(@RequestBody Song song) {
        logger.info("uploadSong method called");
        return ResponseEntity.ok(Map.of("success", (Object) songService.uploadSong(song)));
    }

    @PutMapping("/{songId}")
    public ResponseEntity<Map<String, Object>> updateSong(@PathVariable int songId, @RequestBody Song song) {
        logger.info("updateSong method called for songId: {}", songId);
        song.setSongId(songId);
        return ResponseEntity.ok(Map.of("success", (Object) songService.updateSong(song)));
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<Map<String, Object>> deleteSong(@PathVariable int songId) {
        logger.info("deleteSong method called for songId: {}", songId);
        return ResponseEntity.ok(Map.of("success", (Object) songService.deleteSong(songId)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Song>> searchSongs(@RequestParam String q) {
        logger.info("searchSongs method called with query: {}", q);
        return ResponseEntity.ok(songService.searchSongs(q));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Song>> getSongsByArtist(@PathVariable int artistId) {
        logger.info("getSongsByArtist method called for artistId: {}", artistId);
        return ResponseEntity.ok(songService.getSongsByArtist(artistId));
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<Song>> getSongsByAlbum(@PathVariable int albumId) {
        logger.info("getSongsByAlbum method called for albumId: {}", albumId);
        return ResponseEntity.ok(songService.getSongsByAlbum(albumId));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<List<Song>> getSongsByGenre(@PathVariable int genreId) {
        logger.info("getSongsByGenre method called for genreId: {}", genreId);
        return ResponseEntity.ok(songService.getSongsByGenre(genreId));
    }

    @PostMapping("/{songId}/play")
    public ResponseEntity<Map<String, Object>> playSong(@PathVariable int songId,
            @RequestParam(required = false) Integer userId) {
        logger.info("playSong method called for songId: {}", songId);
        songService.playSong(songId, userId);
        return ResponseEntity.ok(Map.of("success", (Object) true));
    }

    @GetMapping("/top")
    public ResponseEntity<List<Song>> getTopSongs(@RequestParam(defaultValue = "10") int limit) {
        logger.info("getTopSongs method called with limit: {}", limit);
        return ResponseEntity.ok(songService.getTopPlayedSongs(limit));
    }

    @PutMapping("/{songId}/visibility")
    public ResponseEntity<Map<String, Object>> toggleVisibility(@PathVariable int songId,
            @RequestBody Map<String, String> body) {
        logger.info("toggleVisibility method called for songId: {}", songId);
        Song song = songService.getSongById(songId);
        if (song == null)
            return ResponseEntity.notFound().build();
        String newStatus = body.getOrDefault("isActive", "Y");
        song.setIsActive(newStatus);
        boolean updated = songService.updateSong(song);
        return ResponseEntity.ok(Map.of("success", (Object) updated, "isActive", (Object) newStatus));
    }
}
