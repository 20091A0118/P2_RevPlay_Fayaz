package com.revplay.app.service;

import com.revplay.app.entity.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for AlbumServiceImpl using mock DAO.
 */
public class AlbumServiceTest {

    private MockAlbumDao mockAlbumDao;
    private AlbumServiceImpl albumService;

    @BeforeEach
    public void setup() {
        mockAlbumDao = new MockAlbumDao();
        albumService = new AlbumServiceImpl();
        albumService.setAlbumDao(mockAlbumDao);
        ReflectionTestUtils.setField(albumService, "artistRepo", new MockArtistDao());
    }

    @Test
    public void testCreateAlbum_Success() {
        Album album = new Album();
        album.setArtistId(1);
        album.setTitle("Test Album");

        boolean result = albumService.createAlbum(album);

        assertTrue(result, "Album creation should succeed");
        assertEquals(1, album.getAlbumId());
    }

    @Test
    public void testGetAlbumById_Found() {
        Album album = new Album();
        album.setTitle("Find Me Album");
        album.setArtistId(1);
        mockAlbumDao.createAlbum(album);

        Album found = albumService.getAlbumById(album.getAlbumId());

        assertNotNull(found);
        assertEquals("Find Me Album", found.getTitle());
    }

    @Test
    public void testGetAlbumById_NotFound() {
        Album found = albumService.getAlbumById(999);

        assertNull(found, "Should return null for non-existent album");
    }

    @Test
    public void testGetArtistAlbums() {
        Album album1 = new Album();
        album1.setArtistId(5);
        album1.setTitle("Album 1");
        mockAlbumDao.createAlbum(album1);

        Album album2 = new Album();
        album2.setArtistId(5);
        album2.setTitle("Album 2");
        mockAlbumDao.createAlbum(album2);

        Album album3 = new Album();
        album3.setArtistId(10);
        album3.setTitle("Other Artist Album");
        mockAlbumDao.createAlbum(album3);

        List<Album> albums = albumService.getAlbumsByArtist(5);

        assertNotNull(albums);
        assertEquals(2, albums.size());
    }

    @Test
    public void testGetAllAlbums() {
        mockAlbumDao.createAlbum(new Album());
        mockAlbumDao.createAlbum(new Album());
        mockAlbumDao.createAlbum(new Album());

        List<Album> all = albumService.getAllAlbums();

        assertNotNull(all);
        assertEquals(3, all.size());
    }
}
