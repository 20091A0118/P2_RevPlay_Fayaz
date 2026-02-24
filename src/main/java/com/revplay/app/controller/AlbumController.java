package com.revplay.app.controller;

import com.revplay.app.entity.Album;
import com.revplay.app.service.IAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    @Autowired
    private IAlbumService albumService;

    // CREATE - Create new album
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAlbum(@RequestBody Album album) {
        boolean success = albumService.createAlbum(album);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Album created successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to create album", "success", false));
    }

    // READ - Get all albums
    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    // READ - Get album by ID
    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable int id) {
        Album album = albumService.getAlbumById(id);
        if (album != null) {
            return ResponseEntity.ok(album);
        }
        return ResponseEntity.notFound().build();
    }

    // READ - Get albums by artist ID
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Album>> getAlbumsByArtistId(@PathVariable int artistId) {
        return ResponseEntity.ok(albumService.getAlbumsByArtistId(artistId));
    }

    // UPDATE - Update album
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAlbum(@PathVariable int id, @RequestBody Album album) {
        album.setAlbumId(id);
        boolean success = albumService.updateAlbum(album);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Album updated successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to update album", "success", false));
    }

    // DELETE - Delete album
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAlbum(@PathVariable int id) {
        boolean success = albumService.deleteAlbum(id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Album deleted successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to delete album", "success", false));
    }
}
