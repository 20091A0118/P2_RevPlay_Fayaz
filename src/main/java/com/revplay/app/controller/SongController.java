package com.revplay.app.controller;

import com.revplay.app.entity.Song;
import com.revplay.app.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Autowired
    private ISongService songService;

    // CREATE - Add new song
    @PostMapping
    public ResponseEntity<Map<String, Object>> addSong(@RequestBody Song song) {
        boolean success = songService.addSong(song);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Song added successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to add song", "success", false));
    }

    // READ - Get all songs
    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    // READ - Get song by ID
    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable int id) {
        Song song = songService.getSongById(id);
        if (song != null) {
            return ResponseEntity.ok(song);
        }
        return ResponseEntity.notFound().build();
    }

    // READ - Get songs by artist ID
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Song>> getSongsByArtist(@PathVariable int artistId) {
        return ResponseEntity.ok(songService.getSongsByArtistId(artistId));
    }

    // READ - Get songs by album ID
    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<Song>> getSongsByAlbum(@PathVariable int albumId) {
        return ResponseEntity.ok(songService.getSongsByAlbumId(albumId));
    }

    // READ - Get songs by genre ID
    @GetMapping("/genre/{genreId}")
    public ResponseEntity<List<Song>> getSongsByGenre(@PathVariable int genreId) {
        return ResponseEntity.ok(songService.getSongsByGenreId(genreId));
    }

    // READ - Search songs by keyword
    @GetMapping("/search")
    public ResponseEntity<List<Song>> searchSongs(@RequestParam String keyword) {
        return ResponseEntity.ok(songService.searchSongs(keyword));
    }

    // UPDATE - Update song
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSong(@PathVariable int id, @RequestBody Song song) {
        song.setSongId(id);
        boolean success = songService.updateSong(song);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Song updated successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to update song", "success", false));
    }

    // DELETE - Delete song
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSong(@PathVariable int id) {
        boolean success = songService.deleteSong(id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Song deleted successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to delete song", "success", false));
    }

    // UPDATE - Increment play count
    @PutMapping("/{id}/play")
    public ResponseEntity<Map<String, Object>> incrementPlayCount(@PathVariable int id) {
        boolean success = songService.incrementPlayCount(id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Play count incremented", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to increment play count", "success", false));
    }

    // READ - Get top played songs
    @GetMapping("/top")
    public ResponseEntity<List<Song>> getTopPlayedSongs(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(songService.getTopPlayedSongs(limit));
    }

    // READ - Count songs by artist
    @GetMapping("/artist/{artistId}/count")
    public ResponseEntity<Map<String, Object>> countSongsByArtist(@PathVariable int artistId) {
        int count = songService.countSongsByArtist(artistId);
        return ResponseEntity.ok(Map.of("artistId", artistId, "songCount", count));
    }
}
