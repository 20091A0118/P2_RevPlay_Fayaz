package com.revplay.app.controller;

import com.revplay.app.entity.UserAccount;
import com.revplay.app.service.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserAccountController {

    @Autowired
    private IUserAccountService userService;

    // CREATE - Register new user
    @PostMapping
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UserAccount user) {
        boolean success = userService.registerUser(user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "User registered successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to register user", "success", false));
    }

    // READ - Get all users
    @GetMapping
    public ResponseEntity<List<UserAccount>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // READ - Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserAccount> getUserById(@PathVariable int id) {
        UserAccount user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // READ - Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserAccount> getUserByEmail(@PathVariable String email) {
        UserAccount user = userService.getUserByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // UPDATE - Update user
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable int id, @RequestBody UserAccount user) {
        user.setUserId(id);
        boolean success = userService.updateUser(user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "User updated successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to update user", "success", false));
    }

    // READ - Get playlist count for user
    @GetMapping("/{id}/playlist-count")
    public ResponseEntity<Map<String, Object>> getPlaylistCount(@PathVariable int id) {
        int count = userService.getPlaylistCount(id);
        return ResponseEntity.ok(Map.of("userId", id, "playlistCount", count));
    }

    // READ - Get favorite count for user
    @GetMapping("/{id}/favorite-count")
    public ResponseEntity<Map<String, Object>> getFavoriteCount(@PathVariable int id) {
        int count = userService.getFavoriteCount(id);
        return ResponseEntity.ok(Map.of("userId", id, "favoriteCount", count));
    }
}
