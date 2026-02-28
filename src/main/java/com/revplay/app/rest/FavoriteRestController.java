package com.revplay.app.rest;

import com.revplay.app.entity.Song;
import com.revplay.app.service.IFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteRestController {

    @Autowired
    private IFavoriteService favoriteService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Song>> getFavorites(@PathVariable int userId) {
        return ResponseEntity.ok(favoriteService.getFavoriteSongs(userId));
    }

    @PostMapping("/{userId}/{songId}")
    public ResponseEntity<Map<String, Object>> addFavorite(
            @PathVariable int userId, @PathVariable int songId) {
        return ResponseEntity.ok(Map.of("success", (Object) favoriteService.addFavorite(userId, songId)));
    }

    @DeleteMapping("/{userId}/{songId}")
    public ResponseEntity<Map<String, Object>> removeFavorite(
            @PathVariable int userId, @PathVariable int songId) {
        return ResponseEntity.ok(Map.of("success", (Object) favoriteService.removeFavorite(userId, songId)));
    }

    @GetMapping("/{userId}/{songId}")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @PathVariable int userId, @PathVariable int songId) {
        return ResponseEntity.ok(Map.of("isFavorite", (Object) favoriteService.isFavorite(userId, songId)));
    }
}
