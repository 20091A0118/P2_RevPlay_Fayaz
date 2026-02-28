package com.revplay.app.service;

import com.revplay.app.entity.Song;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for SongServiceImpl using mock DAO.
 */
public class SongServiceTest {

    private MockSongDao mockSongDao;
    private SongServiceImpl songService;

    @BeforeEach
    public void setup() {
        mockSongDao = new MockSongDao();
        songService = new SongServiceImpl();
        songService.setSongDao(mockSongDao);
        ReflectionTestUtils.setField(songService, "artistRepo", new MockArtistDao());
        ReflectionTestUtils.setField(songService, "genreRepo", new MockGenreDao());
    }

    @Test
    public void testUploadSong_Success() {
        Song song = new Song();
        song.setTitle("My Song");
        song.setArtistId(1);
        song.setDurationSeconds(180);

        boolean result = songService.uploadSong(song);

        assertTrue(result, "Song upload should succeed");
        assertEquals(1, song.getSongId());
    }

    @Test
    public void testGetSongDetails_Found() {
        Song song = new Song();
        song.setTitle("Find Me Song");
        song.setArtistId(1);
        mockSongDao.addSong(song);

        Song found = songService.getSongById(song.getSongId());

        assertNotNull(found);
        assertEquals("Find Me Song", found.getTitle());
    }

    @Test
    public void testGetSongDetails_NotFound() {
        Song found = songService.getSongById(999);

        assertNull(found, "Should return null for non-existent song");
    }

    @Test
    public void testSearch() {
        Song s1 = new Song();
        s1.setTitle("Love Song");
        s1.setArtistId(1);
        mockSongDao.addSong(s1);

        Song s2 = new Song();
        s2.setTitle("Rock Anthem");
        s2.setArtistId(2);
        mockSongDao.addSong(s2);

        Song s3 = new Song();
        s3.setTitle("Love Story");
        s3.setArtistId(3);
        mockSongDao.addSong(s3);

        List<Song> results = songService.searchSongs("Love");

        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    public void testGetArtistSongs() {
        Song s1 = new Song();
        s1.setTitle("Artist Song 1");
        s1.setArtistId(5);
        mockSongDao.addSong(s1);

        Song s2 = new Song();
        s2.setTitle("Artist Song 2");
        s2.setArtistId(5);
        mockSongDao.addSong(s2);

        Song s3 = new Song();
        s3.setTitle("Other Artist");
        s3.setArtistId(10);
        mockSongDao.addSong(s3);

        List<Song> songs = songService.getSongsByArtist(5);

        assertNotNull(songs);
        assertEquals(2, songs.size());
    }
}
