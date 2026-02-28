package com.revplay.app.service;

import com.revplay.app.entity.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for GenreServiceImpl using mock DAO.
 */
public class GenreServiceTest {

    private MockGenreDao mockGenreDao;
    private GenreServiceImpl genreService;

    @BeforeEach
    public void setup() {
        mockGenreDao = new MockGenreDao();
        genreService = new GenreServiceImpl();
        genreService.setGenreDao(mockGenreDao);
    }

    @Test
    public void testGetOrCreateGenre_ExistingGenre() {
        mockGenreDao.addGenre("Rock");

        Genre g = genreService.getOrCreateGenre("Rock");

        assertNotNull(g);
        assertEquals("Rock", g.getGenreName());
    }

    @Test
    public void testGetOrCreateGenre_NewGenre() {
        Genre g = genreService.getOrCreateGenre("Jazz");

        assertNotNull(g);
        assertEquals("Jazz", g.getGenreName());
        assertNotNull(mockGenreDao.getGenreByName("Jazz"));
    }

    @Test
    public void testGetOrCreateGenre_CaseInsensitive() {
        mockGenreDao.addGenre("HipHop");

        Genre g = genreService.getOrCreateGenre("hiphop");

        assertNotNull(g);
        assertEquals("HipHop", g.getGenreName());
    }

    @Test
    public void testGetAllGenres() {
        mockGenreDao.addGenre("Pop");
        mockGenreDao.addGenre("Classical");
        mockGenreDao.addGenre("Electronic");

        List<Genre> genres = genreService.getAllGenres();

        assertNotNull(genres);
        assertEquals(3, genres.size());
    }
}
