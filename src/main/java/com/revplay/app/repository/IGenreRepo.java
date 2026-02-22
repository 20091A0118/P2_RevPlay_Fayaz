package com.revplay.app.repository;

import com.revplay.app.entity.Genre;
import java.util.List;

public interface IGenreRepo {
    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    Genre getGenreByName(String name);

    boolean addGenre(String genreName);

    Genre getOrCreateGenre(String genreName);
}
