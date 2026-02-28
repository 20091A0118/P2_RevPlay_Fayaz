package com.revplay.app.service;

import com.revplay.app.repository.IUserAccountRepo;
import com.revplay.app.repository.IPlaylistRepo;
import com.revplay.app.repository.IFavoriteRepo;
import com.revplay.app.entity.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserAccountServiceImpl implements IUserAccountService {

    @Autowired
    private IUserAccountRepo userAccountDao;

    @Autowired
    private IPlaylistRepo playlistRepo;

    @Autowired
    private IFavoriteRepo favoriteRepo;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public void setUserDao(IUserAccountRepo userAccountDao) {
        this.userAccountDao = userAccountDao;
    }

    public Map<String, Object> registerUser(UserAccount user) {
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
        return userAccountDao.findById(userId).orElse(null);
    }

    public UserAccount getUserByEmail(String email) {
        return userAccountDao.findByEmail(email).orElse(null);
    }

    public boolean updateProfile(UserAccount user) {
        try {
            userAccountDao.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getUserStats(int userId) {
        long playlistCount = playlistRepo.countByUserId(userId);
        long favoriteCount = favoriteRepo.countByUserId(userId);
        return Map.of(
                "playlistCount", playlistCount,
                "favoriteCount", favoriteCount);
    }

    public Map<String, Object> forgotPassword(String email, String securityAnswer, String newPassword) {
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
        return userAccountDao.findByEmail(email)
                .map(user -> Map.of("success", (Object) true,
                        "securityQuestion", user.getSecurityQuestion() != null ? user.getSecurityQuestion() : "",
                        "passwordHint", user.getPasswordHint() != null ? user.getPasswordHint() : ""))
                .orElse(Map.of("success", (Object) false, "message", "Email not found"));
    }

    public boolean updatePassword(int userId, String oldPassword, String newPassword) {
        return userAccountDao.findById(userId)
                .map(user -> {
                    if (user.getPasswordHash().equals(oldPassword)) {
                        user.setPasswordHash(newPassword);
                        userAccountDao.save(user);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }
}
