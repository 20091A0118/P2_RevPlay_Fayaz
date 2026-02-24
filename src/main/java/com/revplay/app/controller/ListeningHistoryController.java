package com.revplay.app.controller;

import com.revplay.app.entity.ListeningHistory;
import com.revplay.app.service.IListeningHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class ListeningHistoryController {

    @Autowired
    private IListeningHistoryService historyService;

    // CREATE - Add listening history entry
    @PostMapping("/{userId}/{songId}")
    public ResponseEntity<Map<String, Object>> addHistory(@PathVariable int userId, @PathVariable int songId) {
        boolean success = historyService.addHistory(userId, songId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "History entry added", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to add history", "success", false));
    }

    // READ - Get user's listening history
    @GetMapping("/{userId}")
    public ResponseEntity<List<ListeningHistory>> getUserHistory(@PathVariable int userId) {
        return ResponseEntity.ok(historyService.getUserHistory(userId));
    }

    // DELETE - Clear user's listening history
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> clearHistory(@PathVariable int userId) {
        boolean success = historyService.clearHistory(userId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "History cleared successfully", "success", true));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to clear history", "success", false));
    }
}
