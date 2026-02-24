package com.revplay.app.controller;

import com.revplay.app.entity.Song;
import com.revplay.app.service.IFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private IFavoriteService favoriteService;

    // CREATE - Add song to favorites
    @PostMapping("/{userId}/{songId}")
    public ResponseEntity<Map<String, Object>> addFavorite(@PathVariable int userId, @PathVariable int songId) {
        boolean success = favoriteService.addFavorite(userId, songId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Song added to favorites", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to add favorite", "success", false));
    }

    // DELETE - Remove song from favorites
    @DeleteMapping("/{userId}/{songId}")
    public ResponseEntity<Map<String, Object>> removeFavorite(@PathVariable int userId, @PathVariable int songId) {
        boolean success = favoriteService.removeFavorite(userId, songId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Song removed from favorites", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to remove favorite", "success", false));
    }

    // READ - Get user's favorite songs
    @GetMapping("/{userId}")
    public ResponseEntity<List<Song>> getFavoriteSongs(@PathVariable int userId) {
        return ResponseEntity.ok(favoriteService.getFavoriteSongs(userId));
    }

    // READ - Check if song is favorite
    @GetMapping("/{userId}/{songId}")
    public ResponseEntity<Map<String, Object>> isFavorite(@PathVariable int userId, @PathVariable int songId) {
        boolean isFav = favoriteService.isFavorite(userId, songId);
        return ResponseEntity.ok(Map.of("userId", userId, "songId", songId, "isFavorite", isFav));
    }
}
