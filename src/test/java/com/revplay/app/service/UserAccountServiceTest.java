package com.revplay.app.service;

import com.revplay.app.entity.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Unit tests for UserAccountServiceImpl using mock DAO.
 */
public class UserAccountServiceTest {

    private MockUserAccountDao mockUserDao;
    private UserAccountServiceImpl userService;

    @BeforeEach
    public void setup() {
        mockUserDao = new MockUserAccountDao();
        userService = new UserAccountServiceImpl();
        userService.setUserDao(mockUserDao);
        ReflectionTestUtils.setField(userService, "passwordEncoder", new BCryptPasswordEncoder());
    }

    @Test
    public void testRegisterUser_Success() {
        UserAccount user = new UserAccount();
        user.setEmail("test@example.com");
        user.setPasswordHash("password123");
        user.setFullName("Test User");

        Map<String, Object> result = userService.registerUser(user);

        assertTrue((Boolean) result.get("success"), "User should be registered successfully");
        assertNotNull(mockUserDao.getUserAccountByEmail("test@example.com"));
    }

    @Test
    public void testRegisterUser_DuplicateEmail() {
        UserAccount user1 = new UserAccount();
        user1.setEmail("duplicate@example.com");
        user1.setPasswordHash("pass1");
        user1.setFullName("User 1");
        userService.registerUser(user1);

        UserAccount user2 = new UserAccount();
        user2.setEmail("duplicate@example.com");
        user2.setPasswordHash("pass2");
        user2.setFullName("User 2");

        Map<String, Object> result = userService.registerUser(user2);

        assertFalse((Boolean) result.get("success"), "Should not allow duplicate email registration");
    }

    @Test
    public void testGetUserByEmail_Found() {
        UserAccount user = new UserAccount();
        user.setEmail("find@example.com");
        user.setFullName("Find Me");
        mockUserDao.addUserAccount(user);

        UserAccount found = userService.getUserByEmail("find@example.com");

        assertNotNull(found);
        assertEquals("Find Me", found.getFullName());
    }

    @Test
    public void testGetUserByEmail_NotFound() {
        UserAccount user = userService.getUserByEmail("nonexistent@example.com");

        assertNull(user, "Should return null for non-existent user");
    }

    @Test
    public void testUpdateProfile_Success() {
        UserAccount user = new UserAccount();
        user.setEmail("update@example.com");
        user.setFullName("Original Name");
        mockUserDao.addUserAccount(user);

        user.setFullName("Updated Name");
        boolean result = userService.updateProfile(user);

        assertTrue(result);
        assertEquals("Updated Name", mockUserDao.getUserAccount(user.getUserId()).getFullName());
    }
}
