package com.revplay.app.controller;

import com.revplay.app.entity.Genre;
import com.revplay.app.service.IGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    @Autowired
    private IGenreService genreService;

    // READ - Get all genres
    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    // READ - Get genre by ID
    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable int id) {
        Genre genre = genreService.getGenreById(id);
        if (genre != null) {
            return ResponseEntity.ok(genre);
        }
        return ResponseEntity.notFound().build();
    }

    // CREATE - Add new genre
    @PostMapping
    public ResponseEntity<Map<String, Object>> addGenre(@RequestBody Map<String, String> request) {
        String genreName = request.get("genreName");
        if (genreName == null || genreName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Genre name is required", "success", false));
        }
        boolean success = genreService.addGenre(genreName);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Genre added successfully", "success", true));
        }
        return ResponseEntity.badRequest()
                .body(Map.of("message", "Failed to add genre (may already exist)", "success", false));
    }

    // READ - Get genre by name
    @GetMapping("/name/{name}")
    public ResponseEntity<Genre> getGenreByName(@PathVariable String name) {
        Genre genre = genreService.getGenreByName(name);
        if (genre != null) {
            return ResponseEntity.ok(genre);
        }
        return ResponseEntity.notFound().build();
    }
}
