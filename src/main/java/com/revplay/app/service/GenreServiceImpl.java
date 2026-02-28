package com.revplay.app.service;

import com.revplay.app.repository.IGenreRepo;
import com.revplay.app.entity.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements IGenreService {

    @Autowired
    private IGenreRepo genreDao;

    public void setGenreDao(IGenreRepo genreDao) {
        this.genreDao = genreDao;
    }

    public List<Genre> getAllGenres() {
        return genreDao.findAll();
    }

    public Genre getGenreById(int id) {
        return genreDao.findById(id).orElse(null);
    }

    public Genre getOrCreateGenre(String genreName) {
        return genreDao.findByGenreName(genreName)
                .orElseGet(() -> {
                    Genre newGenre = new Genre();
                    newGenre.setGenreName(genreName);
                    return genreDao.save(newGenre);
                });
    }
}
