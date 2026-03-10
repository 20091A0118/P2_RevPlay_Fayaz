package com.revplay.app.service;

import com.revplay.app.entity.Genre;
import java.util.List;

public interface IGenreService {
    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    Genre getOrCreateGenre(String genreName);
}
