package com.revplay.app.rest;

import com.revplay.app.entity.ListeningHistory;
import com.revplay.app.service.IHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class HistoryRestController {

    @Autowired
    private IHistoryService historyService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<ListeningHistory>> getUserHistory(@PathVariable int userId) {
        return ResponseEntity.ok(historyService.getUserHistory(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> clearHistory(@PathVariable int userId) {
        return ResponseEntity.ok(Map.of("success", (Object) historyService.clearHistory(userId)));
    }
}
