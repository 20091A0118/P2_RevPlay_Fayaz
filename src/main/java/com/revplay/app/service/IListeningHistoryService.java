package com.revplay.app.service;

import com.revplay.app.entity.ListeningHistory;

import java.util.List;

public interface IListeningHistoryService {
    boolean addHistory(int userId, int songId);

    List<ListeningHistory> getUserHistory(int userId);

    boolean clearHistory(int userId);
}
