package com.revplay.app.controller;

import com.revplay.app.entity.ArtistAccount;
import com.revplay.app.service.IArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/artists")
public class ArtistAccountController {

    @Autowired
    private IArtistService artistService;

    // CREATE - Register new artist
    @PostMapping
    public ResponseEntity<Map<String, Object>> registerArtist(@RequestBody ArtistAccount artist) {
        boolean success = artistService.registerArtist(artist);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Artist registered successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to register artist", "success", false));
    }

    // READ - Get all artists
    @GetMapping
    public ResponseEntity<List<ArtistAccount>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    // READ - Get artist by ID
    @GetMapping("/{id}")
    public ResponseEntity<ArtistAccount> getArtistById(@PathVariable int id) {
        ArtistAccount artist = artistService.getArtistById(id);
        if (artist != null) {
            return ResponseEntity.ok(artist);
        }
        return ResponseEntity.notFound().build();
    }

    // READ - Get artist by email
    @GetMapping("/email/{email}")
    public ResponseEntity<ArtistAccount> getArtistByEmail(@PathVariable String email) {
        ArtistAccount artist = artistService.getArtistByEmail(email);
        if (artist != null) {
            return ResponseEntity.ok(artist);
        }
        return ResponseEntity.notFound().build();
    }

    // UPDATE - Update artist profile
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateArtist(@PathVariable int id, @RequestBody ArtistAccount artist) {
        artist.setArtistId(id);
        boolean success = artistService.updateArtist(artist);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Artist updated successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to update artist", "success", false));
    }

    // READ - Search artists by keyword
    @GetMapping("/search")
    public ResponseEntity<List<ArtistAccount>> searchArtists(@RequestParam String keyword) {
        return ResponseEntity.ok(artistService.searchArtists(keyword));
    }

    // READ - Get total plays for artist
    @GetMapping("/{id}/total-plays")
    public ResponseEntity<Map<String, Object>> getTotalPlays(@PathVariable int id) {
        int totalPlays = artistService.getTotalPlays(id);
        return ResponseEntity.ok(Map.of("artistId", id, "totalPlays", totalPlays));
    }

    // READ - Get total favorites for artist
    @GetMapping("/{id}/total-favorites")
    public ResponseEntity<Map<String, Object>> getTotalFavorites(@PathVariable int id) {
        int totalFavorites = artistService.getTotalFavorites(id);
        return ResponseEntity.ok(Map.of("artistId", id, "totalFavorites", totalFavorites));
    }
}
