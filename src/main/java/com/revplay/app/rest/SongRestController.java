package com.revplay.app.rest;

import com.revplay.app.entity.Song;
import com.revplay.app.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/songs")
public class SongRestController {

    @Autowired
    private ISongService songService;

    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @GetMapping("/{songId}")
    public ResponseEntity<?> getSongById(@PathVariable int songId) {
        Song song = songService.getSongById(songId);
        if (song == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(song);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadSong(@RequestBody Song song) {
        return ResponseEntity.ok(Map.of("success", (Object) songService.uploadSong(song)));
    }

    @PutMapping("/{songId}")
    public ResponseEntity<Map<String, Object>> updateSong(@PathVariable int songId, @RequestBody Song song) {
        song.setSongId(songId);
        return ResponseEntity.ok(Map.of("success", (Object) songService.updateSong(song)));
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<Map<String, Object>> deleteSong(@PathVariable int songId) {
        return ResponseEntity.ok(Map.of("success", (Object) songService.deleteSong(songId)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Song>> searchSongs(@RequestParam String q) {
        return ResponseEntity.ok(songService.searchSongs(q));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Song>> getSongsByArtist(@PathVariable int artistId) {
        return ResponseEntity.ok(songService.getSongsByArtist(artistId));
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<Song>> getSongsByAlbum(@PathVariable int albumId) {
        return ResponseEntity.ok(songService.getSongsByAlbum(albumId));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<List<Song>> getSongsByGenre(@PathVariable int genreId) {
        return ResponseEntity.ok(songService.getSongsByGenre(genreId));
    }

    @PostMapping("/{songId}/play")
    public ResponseEntity<Map<String, Object>> playSong(@PathVariable int songId,
            @RequestParam(required = false) Integer userId) {
        songService.playSong(songId, userId);
        return ResponseEntity.ok(Map.of("success", (Object) true));
    }

    @GetMapping("/top")
    public ResponseEntity<List<Song>> getTopSongs(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(songService.getTopPlayedSongs(limit));
    }
}
