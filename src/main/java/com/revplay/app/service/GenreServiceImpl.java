package com.revplay.app.service;

import com.revplay.app.entity.Genre;
import com.revplay.app.repository.IGenreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements IGenreService {

    @Autowired
    private IGenreRepo genreRepo;

    @Override
    public List<Genre> getAllGenres() {
        return genreRepo.getAllGenres();
    }

    @Override
    public Genre getGenreById(int id) {
        return genreRepo.getGenreById(id);
    }

    @Override
    public Genre getGenreByName(String name) {
        return genreRepo.getGenreByName(name);
    }

    @Override
    public boolean addGenre(String genreName) {
        return genreRepo.addGenre(genreName);
    }

    @Override
    public Genre getOrCreateGenre(String genreName) {
        return genreRepo.getOrCreateGenre(genreName);
    }
}
