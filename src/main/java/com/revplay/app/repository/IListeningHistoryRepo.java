package com.revplay.app.repository;

import com.revplay.app.entity.ListeningHistory;
import java.util.List;

public interface IListeningHistoryRepo {
    boolean addHistory(int userId, int songId);

    List<ListeningHistory> getUserHistory(int userId);

    boolean clearHistory(int userId);
}
