package com.revplay.app.rest;

import com.revplay.app.entity.Playlist;
import com.revplay.app.entity.Song;
import com.revplay.app.service.IPlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistRestController {

    @Autowired
    private IPlaylistService playlistService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Playlist>> getUserPlaylists(@PathVariable int userId) {
        return ResponseEntity.ok(playlistService.getUserPlaylists(userId));
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<?> getPlaylistById(@PathVariable int playlistId) {
        Playlist p = playlistService.getPlaylistById(playlistId);
        if (p == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }

    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<List<Song>> getPlaylistSongs(@PathVariable int playlistId) {
        return ResponseEntity.ok(playlistService.getPlaylistSongs(playlistId));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPlaylist(@RequestBody Playlist playlist) {
        return ResponseEntity.ok(Map.of("success", (Object) playlistService.createPlaylist(playlist)));
    }

    @PutMapping("/{playlistId}")
    public ResponseEntity<Map<String, Object>> updatePlaylist(@PathVariable int playlistId,
            @RequestBody Playlist playlist) {
        playlist.setPlaylistId(playlistId);
        return ResponseEntity.ok(Map.of("success", (Object) playlistService.updatePlaylist(playlist)));
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Map<String, Object>> deletePlaylist(@PathVariable int playlistId) {
        return ResponseEntity.ok(Map.of("success", (Object) playlistService.deletePlaylist(playlistId)));
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Map<String, Object>> addSongToPlaylist(
            @PathVariable int playlistId, @PathVariable int songId) {
        return ResponseEntity.ok(Map.of("success", (Object) playlistService.addSongToPlaylist(playlistId, songId)));
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Map<String, Object>> removeSongFromPlaylist(
            @PathVariable int playlistId, @PathVariable int songId) {
        return ResponseEntity
                .ok(Map.of("success", (Object) playlistService.removeSongFromPlaylist(playlistId, songId)));
    }
}
