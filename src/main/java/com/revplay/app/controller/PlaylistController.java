package com.revplay.app.controller;

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
public class PlaylistController {

    @Autowired
    private IPlaylistService playlistService;

    // CREATE - Create new playlist
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPlaylist(@RequestBody Playlist playlist) {
        boolean success = playlistService.createPlaylist(playlist);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Playlist created successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to create playlist", "success", false));
    }

    // READ - Get playlists by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Playlist>> getPlaylistsByUser(@PathVariable int userId) {
        return ResponseEntity.ok(playlistService.getPlaylistsByUserId(userId));
    }

    // READ - Get public playlists
    @GetMapping("/public")
    public ResponseEntity<List<Playlist>> getPublicPlaylists() {
        return ResponseEntity.ok(playlistService.getPublicPlaylists());
    }

    // READ - Get playlist by ID
    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylistById(@PathVariable int id) {
        Playlist playlist = playlistService.getPlaylistById(id);
        if (playlist != null) {
            return ResponseEntity.ok(playlist);
        }
        return ResponseEntity.notFound().build();
    }

    // UPDATE - Update playlist
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePlaylist(@PathVariable int id, @RequestBody Playlist playlist) {
        playlist.setPlaylistId(id);
        boolean success = playlistService.updatePlaylist(playlist);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Playlist updated successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to update playlist", "success", false));
    }

    // DELETE - Delete playlist
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePlaylist(@PathVariable int id) {
        boolean success = playlistService.deletePlaylist(id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Playlist deleted successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to delete playlist", "success", false));
    }

    // CREATE - Add song to playlist
    @PostMapping("/{id}/songs/{songId}")
    public ResponseEntity<Map<String, Object>> addSongToPlaylist(@PathVariable int id, @PathVariable int songId) {
        boolean success = playlistService.addSongToPlaylist(id, songId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Song added to playlist", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to add song to playlist", "success", false));
    }

    // DELETE - Remove song from playlist
    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<Map<String, Object>> removeSongFromPlaylist(@PathVariable int id, @PathVariable int songId) {
        boolean success = playlistService.removeSongFromPlaylist(id, songId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Song removed from playlist", "success", true));
        }
        return ResponseEntity.badRequest()
                .body(Map.of("message", "Failed to remove song from playlist", "success", false));
    }

    // READ - Get songs in playlist
    @GetMapping("/{id}/songs")
    public ResponseEntity<List<Song>> getSongsInPlaylist(@PathVariable int id) {
        return ResponseEntity.ok(playlistService.getSongsInPlaylist(id));
    }
}
