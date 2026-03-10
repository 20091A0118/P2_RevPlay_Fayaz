package com.revplay.app.rest;

import com.revplay.app.entity.Genre;
import com.revplay.app.service.IGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreRestController {

    private static final Logger logger = LoggerFactory.getLogger(GenreRestController.class);

    @Autowired
    private IGenreService genreService;

    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        logger.info("getAllGenres method called");
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<?> getGenreById(@PathVariable int genreId) {
        logger.info("getGenreById method called for genreId: {}", genreId);
        Genre genre = genreService.getGenreById(genreId);
        if (genre == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(genre);
    }

    @PostMapping
    public ResponseEntity<Genre> createGenre(@RequestBody Genre genre) {
        logger.info("createGenre method called for genreName: {}", genre.getGenreName());
        Genre created = genreService.getOrCreateGenre(genre.getGenreName());
        return ResponseEntity.ok(created);
    }
}
