package com.revplay.app.rest;

import com.revplay.app.entity.Album;
import com.revplay.app.entity.Song;
import com.revplay.app.service.IAlbumService;
import com.revplay.app.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/albums")
public class AlbumRestController {

    @Autowired
    private IAlbumService albumService;

    @Autowired
    private ISongService songService;

    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<?> getAlbumById(@PathVariable int albumId) {
        Album album = albumService.getAlbumById(albumId);
        if (album == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(album);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAlbum(@RequestBody Album album) {
        return ResponseEntity.ok(Map.of("success", albumService.createAlbum(album)));
    }

    @PutMapping("/{albumId}")
    public ResponseEntity<Map<String, Object>> updateAlbum(@PathVariable int albumId, @RequestBody Album album) {
        album.setAlbumId(albumId);
        return ResponseEntity.ok(Map.of("success", albumService.updateAlbum(album)));
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Map<String, Object>> deleteAlbum(@PathVariable int albumId) {
        return ResponseEntity.ok(Map.of("success", albumService.deleteAlbum(albumId)));
    }

    @GetMapping("/{albumId}/songs")
    public ResponseEntity<List<Song>> getAlbumSongs(@PathVariable int albumId) {
        return ResponseEntity.ok(songService.getSongsByAlbum(albumId));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Album>> getAlbumsByArtist(@PathVariable int artistId) {
        return ResponseEntity.ok(albumService.getAlbumsByArtist(artistId));
    }
}
