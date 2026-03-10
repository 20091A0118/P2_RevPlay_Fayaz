package com.revplay.app.rest;

import com.revplay.app.entity.Song;
import com.revplay.app.service.IFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteRestController {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteRestController.class);

    @Autowired
    private IFavoriteService favoriteService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Song>> getFavorites(@PathVariable int userId) {
        logger.info("getFavorites method called for userId: {}", userId);
        return ResponseEntity.ok(favoriteService.getFavoriteSongs(userId));
    }

    @PostMapping("/{userId}/{songId}")
    public ResponseEntity<Map<String, Object>> addFavorite(
            @PathVariable int userId, @PathVariable int songId) {
        logger.info("addFavorite method called for userId: {}, songId: {}", userId, songId);
        return ResponseEntity.ok(Map.of("success", (Object) favoriteService.addFavorite(userId, songId)));
    }

    @DeleteMapping("/{userId}/{songId}")
    public ResponseEntity<Map<String, Object>> removeFavorite(
            @PathVariable int userId, @PathVariable int songId) {
        logger.info("removeFavorite method called for userId: {}, songId: {}", userId, songId);
        return ResponseEntity.ok(Map.of("success", (Object) favoriteService.removeFavorite(userId, songId)));
    }

    @GetMapping("/{userId}/{songId}")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @PathVariable int userId, @PathVariable int songId) {
        logger.info("checkFavorite method called for userId: {}, songId: {}", userId, songId);
        return ResponseEntity.ok(Map.of("isFavorite", (Object) favoriteService.isFavorite(userId, songId)));
    }
}
