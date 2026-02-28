package com.revplay.app.rest;

import com.revplay.app.entity.ArtistAccount;
import com.revplay.app.service.IArtistService;
import com.revplay.app.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/artists")
public class ArtistRestController {

    @Autowired
    private IArtistService artistService;

    @Autowired
    private ISongService songService;

    @GetMapping
    public ResponseEntity<List<ArtistAccount>> getAllArtists() {
        List<ArtistAccount> artists = artistService.getAllArtists();
        artists.forEach(a -> a.setPasswordHash(null));
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<?> getArtistById(@PathVariable int artistId) {
        ArtistAccount artist = artistService.getArtistById(artistId);
        if (artist == null)
            return ResponseEntity.notFound().build();
        artist.setPasswordHash(null);
        return ResponseEntity.ok(artist);
    }

    @PutMapping("/{artistId}")
    public ResponseEntity<Map<String, Object>> updateArtist(@PathVariable int artistId,
            @RequestBody ArtistAccount artist) {
        ArtistAccount existing = artistService.getArtistById(artistId);
        if (existing == null)
            return ResponseEntity.notFound().build();
        if (artist.getStageName() != null)
            existing.setStageName(artist.getStageName());
        if (artist.getBio() != null)
            existing.setBio(artist.getBio());
        if (artist.getGenre() != null)
            existing.setGenre(artist.getGenre());
        if (artist.getInstagramLink() != null)
            existing.setInstagramLink(artist.getInstagramLink());
        if (artist.getYoutubeLink() != null)
            existing.setYoutubeLink(artist.getYoutubeLink());
        if (artist.getSpotifyLink() != null)
            existing.setSpotifyLink(artist.getSpotifyLink());
        if (artist.getPasswordHash() != null)
            existing.setPasswordHash(artist.getPasswordHash());
        return ResponseEntity.ok(Map.of("success", (Object) artistService.updateProfile(existing)));
    }

    @GetMapping("/{artistId}/stats")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable int artistId) {
        Map<String, Object> stats = artistService.getArtistStats(artistId);
        stats = new java.util.HashMap<>(stats);
        stats.put("songCount", songService.countSongsByArtist(artistId));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArtistAccount>> searchArtists(@RequestParam String q) {
        List<ArtistAccount> artists = artistService.searchArtists(q);
        artists.forEach(a -> a.setPasswordHash(null));
        return ResponseEntity.ok(artists);
    }
}
