package com.revplay.app.service;

import com.revplay.app.entity.ListeningHistory;
import com.revplay.app.repository.IListeningHistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListeningHistoryServiceImpl implements IListeningHistoryService {

    @Autowired
    private IListeningHistoryRepo listeningHistoryRepo;

    @Override
    public boolean addHistory(int userId, int songId) {
        return listeningHistoryRepo.addHistory(userId, songId);
    }

    @Override
    public List<ListeningHistory> getUserHistory(int userId) {
        return listeningHistoryRepo.getUserHistory(userId);
    }

    @Override
    public boolean clearHistory(int userId) {
        return listeningHistoryRepo.clearHistory(userId);
    }
}
