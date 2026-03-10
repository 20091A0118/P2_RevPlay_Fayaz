package com.revplay.app.rest;

import com.revplay.app.entity.Album;
import com.revplay.app.entity.Song;
import com.revplay.app.service.IAlbumService;
import com.revplay.app.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/albums")
public class AlbumRestController {

    private static final Logger logger = LoggerFactory.getLogger(AlbumRestController.class);

    @Autowired
    private IAlbumService albumService;

    @Autowired
    private ISongService songService;

    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() {
        logger.info("getAllAlbums method called");
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<?> getAlbumById(@PathVariable int albumId) {
        logger.info("getAlbumById method called for albumId: {}", albumId);
        Album album = albumService.getAlbumById(albumId);
        if (album == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(album);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAlbum(@RequestBody Album album) {
        logger.info("createAlbum method called");
        return ResponseEntity.ok(Map.of("success", albumService.createAlbum(album)));
    }

    @PutMapping("/{albumId}")
    public ResponseEntity<Map<String, Object>> updateAlbum(@PathVariable int albumId, @RequestBody Album album) {
        logger.info("updateAlbum method called for albumId: {}", albumId);
        album.setAlbumId(albumId);
        return ResponseEntity.ok(Map.of("success", albumService.updateAlbum(album)));
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Map<String, Object>> deleteAlbum(@PathVariable int albumId) {
        logger.info("deleteAlbum method called for albumId: {}", albumId);
        return ResponseEntity.ok(Map.of("success", albumService.deleteAlbum(albumId)));
    }

    @GetMapping("/{albumId}/songs")
    public ResponseEntity<List<Song>> getAlbumSongs(@PathVariable int albumId) {
        logger.info("getAlbumSongs method called for albumId: {}", albumId);
        return ResponseEntity.ok(songService.getSongsByAlbum(albumId));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Album>> getAlbumsByArtist(@PathVariable int artistId) {
        logger.info("getAlbumsByArtist method called for artistId: {}", artistId);
        return ResponseEntity.ok(albumService.getAlbumsByArtist(artistId));
    }
}
