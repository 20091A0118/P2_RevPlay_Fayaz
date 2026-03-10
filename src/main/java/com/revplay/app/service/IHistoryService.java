package com.revplay.app.service;

import com.revplay.app.entity.ListeningHistory;
import java.util.List;

public interface IHistoryService {
    List<ListeningHistory> getUserHistory(int userId);

    void addHistory(int userId, int songId);

    boolean clearHistory(int userId);
}
