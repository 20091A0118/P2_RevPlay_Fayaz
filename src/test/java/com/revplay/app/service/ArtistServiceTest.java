package com.revplay.app.service;

import com.revplay.app.entity.ArtistAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Unit tests for ArtistServiceImpl using mock DAO.
 */
public class ArtistServiceTest {

    private MockArtistDao mockArtistDao;
    private ArtistServiceImpl artistService;

    @BeforeEach
    public void setup() {
        mockArtistDao = new MockArtistDao();
        artistService = new ArtistServiceImpl();
        artistService.setArtistDao(mockArtistDao);
        ReflectionTestUtils.setField(artistService, "passwordEncoder", new BCryptPasswordEncoder());
    }

    @Test
    public void testRegisterArtist_Success() {
        ArtistAccount artist = new ArtistAccount();
        artist.setEmail("artist@music.com");
        artist.setPasswordHash("secret");
        artist.setStageName("DJ Test");

        Map<String, Object> result = artistService.registerArtist(artist);

        assertTrue((Boolean) result.get("success"), "Artist registration should succeed");
        assertNotNull(mockArtistDao.getArtistByEmail("artist@music.com"));
    }

    @Test
    public void testRegisterArtist_DuplicateEmail() {
        ArtistAccount artist1 = new ArtistAccount();
        artist1.setEmail("same@music.com");
        artist1.setPasswordHash("pass");
        artist1.setStageName("Artist 1");
        artistService.registerArtist(artist1);

        ArtistAccount artist2 = new ArtistAccount();
        artist2.setEmail("same@music.com");
        artist2.setPasswordHash("pass");
        artist2.setStageName("Artist 2");

        Map<String, Object> result = artistService.registerArtist(artist2);

        assertFalse((Boolean) result.get("success"), "Should not allow duplicate email");
    }

    @Test
    public void testLoginArtist_Success() {
        ArtistAccount artist = new ArtistAccount();
        artist.setEmail("login@music.com");
        artist.setPasswordHash("mypassword");
        artist.setStageName("Login Artist");
        artistService.registerArtist(artist);

        Map<String, Object> result = artistService.loginArtist("login@music.com", "mypassword");

        assertTrue((Boolean) result.get("success"), "Login should succeed with correct credentials");
    }

    @Test
    public void testLoginArtist_WrongPassword() {
        ArtistAccount artist = new ArtistAccount();
        artist.setEmail("wrong@music.com");
        artist.setPasswordHash("correct");
        artist.setStageName("Wrong Pass Artist");
        artistService.registerArtist(artist);

        Map<String, Object> result = artistService.loginArtist("wrong@music.com", "incorrect");

        assertFalse((Boolean) result.get("success"), "Login should fail with wrong password");
    }

    @Test
    public void testUpdateProfile_Success() {
        ArtistAccount artist = new ArtistAccount();
        artist.setEmail("profile@music.com");
        artist.setStageName("Original");
        artist.setPasswordHash("pass");
        artistService.registerArtist(artist);
        artist.setArtistId(1); // Mocks commonly use 1 for first item

        artist.setStageName("Updated Stage Name");
        boolean result = artistService.updateProfile(artist);

        assertTrue(result);
        assertEquals("Updated Stage Name", mockArtistDao.getArtistById(artist.getArtistId()).getStageName());
    }
}
