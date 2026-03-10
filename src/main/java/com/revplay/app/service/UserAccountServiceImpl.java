package com.revplay.app.service;

import com.revplay.app.repository.IUserAccountRepository;
import com.revplay.app.repository.IPlaylistRepository;
import com.revplay.app.repository.IFavoriteRepository;
import com.revplay.app.entity.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserAccountServiceImpl implements IUserAccountService {

    private static final Logger logger = LoggerFactory.getLogger(UserAccountServiceImpl.class);

    @Autowired
    private IUserAccountRepository userAccountDao;

    @Autowired
    private IPlaylistRepository playlistRepository;

    @Autowired
    private IFavoriteRepository favoriteRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public void setUserDao(IUserAccountRepository userAccountDao) {
        this.userAccountDao = userAccountDao;
    }

    public Map<String, Object> registerUser(UserAccount user) {
        logger.info("registerUser method called in Service for email: {}", user.getEmail());
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        // Encode password before saving
        if (user.getPasswordHash() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        try {
            UserAccount created = userAccountDao.save(user);
            return new java.util.HashMap<>(
                    Map.of("success", (Object) true, "message", "Registration successful", "userId",
                            created.getUserId()));
        } catch (Exception e) {
            System.err.println("User registration failed: " + e.getMessage());
            return new java.util.HashMap<>(
                    Map.of("success", (Object) false, "message", "Registration failed. Email may already exist."));
        }
    }

    public Map<String, Object> loginUser(String email, String password) {
        logger.info("loginUser method called in Service for email: {}", email);
        return userAccountDao.findByEmail(email)
                .map(user -> {
                    if (passwordEncoder.matches(password, user.getPasswordHash())) {
                        return new java.util.HashMap<>(Map.of("success", (Object) true, "userId", user.getUserId(),
                                "fullName", user.getFullName(), "email", user.getEmail()));
                    }
                    return new java.util.HashMap<>(
                            Map.of("success", (Object) false, "message", "Invalid email or password"));
                })
                .orElse(new java.util.HashMap<>(
                        Map.of("success", (Object) false, "message", "Invalid email or password")));
    }

    public UserAccount getUserById(int userId) {
        logger.info("getUserById method called in Service for userId: {}", userId);
        return userAccountDao.findById(userId).orElse(null);
    }

    public UserAccount getUserByEmail(String email) {
        logger.info("getUserByEmail method called in Service for email: {}", email);
        return userAccountDao.findByEmail(email).orElse(null);
    }

    public boolean updateProfile(UserAccount user) {
        logger.info("updateProfile method called in Service for userId: {}", user.getUserId());
        try {
            userAccountDao.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getUserStats(int userId) {
        logger.info("getUserStats method called in Service for userId: {}", userId);
        long playlistCount = playlistRepository.countByUserId(userId);
        long favoriteCount = favoriteRepository.countByUserId(userId);
        return Map.of(
                "playlistCount", playlistCount,
                "favoriteCount", favoriteCount);
    }

    public Map<String, Object> forgotPassword(String email, String securityAnswer, String newPassword) {
        logger.info("forgotPassword method called in Service for email: {}", email);
        return userAccountDao.findByEmail(email)
                .map(user -> {
                    if (user.getSecurityAnswerHash() != null
                            && user.getSecurityAnswerHash().equalsIgnoreCase(securityAnswer)) {
                        user.setPasswordHash(passwordEncoder.encode(newPassword));
                        userAccountDao.save(user);
                        return Map.of("success", (Object) true, "message", "Password updated successfully");
                    }
                    return Map.of("success", (Object) false, "message", "Security answer is incorrect");
                })
                .orElse(Map.of("success", (Object) false, "message", "Email not found"));
    }

    public Map<String, Object> getSecurityQuestion(String email) {
        logger.info("getSecurityQuestion method called in Service for email: {}", email);
        return userAccountDao.findByEmail(email)
                .map(user -> Map.of("success", (Object) true,
                        "securityQuestion", user.getSecurityQuestion() != null ? user.getSecurityQuestion() : "",
                        "passwordHint", user.getPasswordHint() != null ? user.getPasswordHint() : ""))
                .orElse(Map.of("success", (Object) false, "message", "Email not found"));
    }

    public boolean updatePassword(int userId, String oldPassword, String newPassword) {
        logger.info("updatePassword method called in Service for userId: {}", userId);
        return userAccountDao.findById(userId)
                .map(user -> {
                    if (passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                        user.setPasswordHash(passwordEncoder.encode(newPassword));
                        userAccountDao.save(user);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }
}
