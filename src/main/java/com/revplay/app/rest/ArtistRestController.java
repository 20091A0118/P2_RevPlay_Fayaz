package com.revplay.app.rest;

import com.revplay.app.entity.ArtistAccount;
import com.revplay.app.entity.FavoriteSong;
import com.revplay.app.entity.Song;
import com.revplay.app.entity.UserAccount;
import com.revplay.app.repository.IFavoriteRepository;
import com.revplay.app.repository.IListeningHistoryRepository;
import com.revplay.app.repository.ISongRepository;
import com.revplay.app.repository.IUserAccountRepository;
import com.revplay.app.service.IArtistService;
import com.revplay.app.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/artists")
public class ArtistRestController {

    private static final Logger logger = LoggerFactory.getLogger(ArtistRestController.class);

    @Autowired
    private IArtistService artistService;

    @Autowired
    private ISongService songService;

    @Autowired
    private IFavoriteRepository favoriteRepository;

    @Autowired
    private IListeningHistoryRepository historyRepository;

    @Autowired
    private ISongRepository songRepository;

    @Autowired
    private IUserAccountRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ArtistAccount>> getAllArtists() {
        logger.info("getAllArtists method called");
        List<ArtistAccount> artists = artistService.getAllArtists();
        artists.forEach(a -> a.setPasswordHash(null));
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<?> getArtistById(@PathVariable int artistId) {
        logger.info("getArtistById method called for artistId: {}", artistId);
        ArtistAccount artist = artistService.getArtistById(artistId);
        if (artist == null)
            return ResponseEntity.notFound().build();
        artist.setPasswordHash(null);
        return ResponseEntity.ok(artist);
    }

    @PutMapping("/{artistId}")
    public ResponseEntity<Map<String, Object>> updateArtist(@PathVariable int artistId,
            @RequestBody ArtistAccount artist) {
        logger.info("updateArtist method called for artistId: {}", artistId);
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
        if (artist.getTwitterLink() != null)
            existing.setTwitterLink(artist.getTwitterLink());
        if (artist.getWebsiteLink() != null)
            existing.setWebsiteLink(artist.getWebsiteLink());
        if (artist.getProfilePicture() != null)
            existing.setProfilePicture(artist.getProfilePicture());
        if (artist.getBannerImage() != null)
            existing.setBannerImage(artist.getBannerImage());
        if (artist.getPasswordHash() != null)
            existing.setPasswordHash(artist.getPasswordHash());
        return ResponseEntity.ok(Map.of("success", (Object) artistService.updateProfile(existing)));
    }

    @GetMapping("/{artistId}/stats")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable int artistId) {
        logger.info("getStats method called for artistId: {}", artistId);
        Map<String, Object> stats = artistService.getArtistStats(artistId);
        stats = new java.util.HashMap<>(stats);
        stats.put("songCount", songService.countSongsByArtist(artistId));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArtistAccount>> searchArtists(@RequestParam String q) {
        logger.info("searchArtists method called with query: {}", q);
        List<ArtistAccount> artists = artistService.searchArtists(q);
        artists.forEach(a -> a.setPasswordHash(null));
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{artistId}/songs-by-popularity")
    public ResponseEntity<List<Song>> getSongsByPopularity(@PathVariable int artistId) {
        logger.info("getSongsByPopularity method called for artistId: {}", artistId);
        List<Song> songs = songRepository.findByArtistIdOrderByPlayCountDesc(artistId);
        songService.enrichSongs(songs);
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/{artistId}/favorites")
    public ResponseEntity<List<Map<String, Object>>> getArtistFavorites(@PathVariable int artistId) {
        logger.info("getArtistFavorites method called for artistId: {}", artistId);
        List<FavoriteSong> favorites = favoriteRepository.findFavoritesByArtistId(artistId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (FavoriteSong f : favorites) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("userId", f.getUserId());
            entry.put("songId", f.getSongId());
            entry.put("favoritedAt", f.getFavoritedAt());
            // Enrich with user name and song title
            try {
                UserAccount user = userRepository.findById(f.getUserId()).orElse(null);
                entry.put("userName", user != null ? user.getFullName() : "User #" + f.getUserId());
            } catch (Exception e) {
                entry.put("userName", "User #" + f.getUserId());
            }
            Song song = songService.getSongById(f.getSongId());
            entry.put("songTitle", song != null ? song.getTitle() : "Song #" + f.getSongId());
            result.add(entry);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{artistId}/top-listeners")
    public ResponseEntity<List<Map<String, Object>>> getTopListeners(@PathVariable int artistId) {
        logger.info("getTopListeners method called for artistId: {}", artistId);
        List<Object[]> rows = historyRepository.findTopListenersByArtistId(artistId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> entry = new HashMap<>();
            int userId = (int) row[0];
            long playCount = (long) row[1];
            entry.put("userId", userId);
            entry.put("playCount", playCount);
            try {
                UserAccount user = userRepository.findById(userId).orElse(null);
                entry.put("userName", user != null ? user.getFullName() : "User #" + userId);
            } catch (Exception e) {
                entry.put("userName", "User #" + userId);
            }
            result.add(entry);
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{artistId}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @PathVariable int artistId, @RequestBody Map<String, String> data) {
        logger.info("updatePassword method called for artistId: {}", artistId);
        boolean success = artistService.updatePassword(artistId, data.get("oldPassword"), data.get("newPassword"));
        if (success)
            return ResponseEntity.ok(Map.of("success", (Object) true, "message", "Password updated"));
        return ResponseEntity.ok(Map.of("success", (Object) false, "message", "Old password is incorrect"));
    }
}
