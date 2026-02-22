package com.revplay.app.repository;

import com.revplay.app.entity.Genre;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GenreRepoImpl implements IGenreRepo {

    private static final Logger logger = LogManager.getLogger(GenreRepoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Genre> rowMapper = (rs, rowNum) -> new Genre(rs.getInt("genre_id"),
            rs.getString("genre_name"));

    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM GENRE ORDER BY genre_name", rowMapper);
    }

    public Genre getGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM GENRE WHERE genre_id = ?", rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Genre getGenreByName(String name) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM GENRE WHERE genre_name = ?", rowMapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean addGenre(String genreName) {
        try {
            return jdbcTemplate.update("INSERT INTO GENRE (genre_name) VALUES (?)", genreName) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in addGenre: {}", e.getMessage());
            return false;
        }
    }

    public Genre getOrCreateGenre(String genreName) {
        Genre existing = getGenreByName(genreName);
        if (existing != null)
            return existing;
        addGenre(genreName);
        return getGenreByName(genreName);
    }
}
