package com.revplay.app.rest;

import com.revplay.app.entity.UserAccount;
import com.revplay.app.service.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);

    @Autowired
    private IUserAccountService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable int userId) {
        logger.info("getUserById method called for userId: {}", userId);
        UserAccount user = userService.getUserById(userId);
        if (user == null)
            return ResponseEntity.notFound().build();
        user.setPasswordHash(null);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateProfile(@PathVariable int userId, @RequestBody UserAccount user) {
        logger.info("updateProfile method called for userId: {}", userId);
        user.setUserId(userId);
        UserAccount existing = userService.getUserById(userId);
        if (existing == null)
            return ResponseEntity.notFound().build();
        if (user.getFullName() != null)
            existing.setFullName(user.getFullName());
        if (user.getPhone() != null)
            existing.setPhone(user.getPhone());
        if (user.getBio() != null)
            existing.setBio(user.getBio());
        if (user.getProfilePicture() != null)
            existing.setProfilePicture(user.getProfilePicture());
        if (user.getPasswordHash() != null)
            existing.setPasswordHash(user.getPasswordHash());
        boolean updated = userService.updateProfile(existing);
        return ResponseEntity.ok(Map.of("success", (Object) updated));
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @PathVariable int userId, @RequestBody Map<String, String> data) {
        logger.info("updatePassword method called for userId: {}", userId);
        boolean success = userService.updatePassword(userId, data.get("oldPassword"), data.get("newPassword"));
        if (success)
            return ResponseEntity.ok(Map.of("success", (Object) true, "message", "Password updated"));
        return ResponseEntity.ok(Map.of("success", (Object) false, "message", "Old password is incorrect"));
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable int userId) {
        logger.info("getUserStats method called for userId: {}", userId);
        return ResponseEntity.ok(userService.getUserStats(userId));
    }
}
