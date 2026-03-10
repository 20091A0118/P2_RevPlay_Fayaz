package com.revplay.app.config;

import com.revplay.app.entity.Genre;
import com.revplay.app.repository.IGenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private IGenreRepository genreRepository;

    @Override
    public void run(String... args) {
        // Seed default genres if none exist
        if (genreRepository.count() == 0) {
            List<String> defaultGenres = List.of(
                    "Pop", "Rock", "Hip-Hop", "R&B", "Jazz", "Classical", "Electronic");
            for (String name : defaultGenres) {
                Genre g = new Genre();
                g.setGenreName(name);
                genreRepository.save(g);
            }
            System.out.println("Seeded " + defaultGenres.size() + " default genres.");
        }
    }
}
