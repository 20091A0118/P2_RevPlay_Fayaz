package com.revplay.app.rest;

import com.revplay.app.entity.ArtistAccount;
import com.revplay.app.entity.UserAccount;
import com.revplay.app.config.JwtUtil;
import com.revplay.app.service.IArtistService;
import com.revplay.app.service.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private IUserAccountService userService;

    @Autowired
    private IArtistService artistService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/user/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UserAccount user) {
        Map<String, Object> result = userService.registerUser(user);
        if (Boolean.TRUE.equals(result.get("success"))) {
            UserAccount registered = userService.getUserByEmail(user.getEmail());
            if (registered != null) {
                String token = jwtUtil.generateToken(registered.getEmail(), "USER");
                result.put("token", token);
                result.put("userId", registered.getUserId());
                result.put("fullName", registered.getFullName());
                result.put("role", "USER");
            }
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> credentials) {
        Map<String, Object> result = userService.loginUser(credentials.get("email"), credentials.get("password"));
        if (Boolean.TRUE.equals(result.get("success"))) {
            String token = jwtUtil.generateToken(credentials.get("email"), "USER");
            result.put("token", token);
            result.put("role", "USER");
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/artist/register")
    public ResponseEntity<Map<String, Object>> registerArtist(@RequestBody ArtistAccount artist) {
        Map<String, Object> result = artistService.registerArtist(artist);
        if (Boolean.TRUE.equals(result.get("success"))) {
            ArtistAccount registered = artistService.getArtistById((int) result.get("artistId"));
            if (registered != null) {
                String token = jwtUtil.generateToken(registered.getEmail(), "ARTIST");
                result.put("token", token);
                result.put("role", "ARTIST");
                result.put("stageName", registered.getStageName());
            }
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/artist/login")
    public ResponseEntity<Map<String, Object>> loginArtist(@RequestBody Map<String, String> credentials) {
        Map<String, Object> result = artistService.loginArtist(credentials.get("email"), credentials.get("password"));
        if (Boolean.TRUE.equals(result.get("success"))) {
            String token = jwtUtil.generateToken(credentials.get("email"), "ARTIST");
            result.put("token", token);
            result.put("role", "ARTIST");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/security-question")
    public ResponseEntity<Map<String, Object>> getSecurityQuestion(@RequestParam String email) {
        return ResponseEntity.ok(userService.getSecurityQuestion(email));
    }

    @PostMapping("/user/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> data) {
        return ResponseEntity.ok(userService.forgotPassword(
                data.get("email"), data.get("securityAnswer"), data.get("newPassword")));
    }

    @GetMapping("/artist/security-question")
    public ResponseEntity<Map<String, Object>> getArtistSecurityQuestion(@RequestParam String email) {
        return ResponseEntity.ok(artistService.getSecurityQuestion(email));
    }

    @PostMapping("/artist/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotArtistPassword(@RequestBody Map<String, String> data) {
        return ResponseEntity.ok(artistService.forgotPassword(
                data.get("email"), data.get("securityAnswer"), data.get("newPassword")));
    }
}
