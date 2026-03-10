package com.revplay.app.service;

import com.revplay.app.repository.IGenreRepository;
import com.revplay.app.entity.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GenreServiceImpl implements IGenreService {

    private static final Logger logger = LoggerFactory.getLogger(GenreServiceImpl.class);

    @Autowired
    private IGenreRepository genreDao;

    public void setGenreDao(IGenreRepository genreDao) {
        this.genreDao = genreDao;
    }

    public List<Genre> getAllGenres() {
        logger.info("getAllGenres method called in Service");
        return genreDao.findAll();
    }

    public Genre getGenreById(int id) {
        logger.info("getGenreById method called in Service for id: {}", id);
        return genreDao.findById(id).orElse(null);
    }

    public Genre getOrCreateGenre(String genreName) {
        logger.info("getOrCreateGenre method called in Service for genreName: {}", genreName);
        return genreDao.findByGenreName(genreName)
                .orElseGet(() -> {
                    Genre newGenre = new Genre();
                    newGenre.setGenreName(genreName);
                    return genreDao.save(newGenre);
                });
    }
}
