package com.revplay.app.rest;

import com.revplay.app.entity.ListeningHistory;
import com.revplay.app.service.IHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class HistoryRestController {

    private static final Logger logger = LoggerFactory.getLogger(HistoryRestController.class);

    @Autowired
    private IHistoryService historyService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<ListeningHistory>> getUserHistory(@PathVariable int userId) {
        logger.info("getUserHistory method called for userId: {}", userId);
        return ResponseEntity.ok(historyService.getUserHistory(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> clearHistory(@PathVariable int userId) {
        logger.info("clearHistory method called for userId: {}", userId);
        return ResponseEntity.ok(Map.of("success", (Object) historyService.clearHistory(userId)));
    }
}
