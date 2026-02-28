package com.revplay.app.service;

import com.revplay.app.entity.ArtistAccount;
import com.revplay.app.entity.Song;
import com.revplay.app.repository.IArtistRepo;
import com.revplay.app.repository.IListeningHistoryRepo;
import com.revplay.app.entity.ListeningHistory;
import com.revplay.app.repository.ISongRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HistoryServiceImpl implements IHistoryService {

    @Autowired
    private IListeningHistoryRepo historyRepo;

    @Autowired
    private ISongRepo songRepo;

    @Autowired
    private IArtistRepo artistRepo;

    public List<ListeningHistory> getUserHistory(int userId) {
        List<ListeningHistory> history = historyRepo.findByUserIdOrderByPlayedAtDesc(userId);
        enrichHistory(history);
        return history;
    }

    public void addHistory(int userId, int songId) {
        ListeningHistory history = new ListeningHistory();
        history.setUserId(userId);
        history.setSongId(songId);
        history.setPlayedAt(LocalDateTime.now());
        history.setActionType("PLAYED");
        historyRepo.save(history);
    }

    private void enrichHistory(List<ListeningHistory> history) {
        if (history == null || history.isEmpty())
            return;
        Map<Integer, String> songTitles = songRepo.findAll().stream()
                .collect(Collectors.toMap(Song::getSongId, Song::getTitle, (a, b) -> a));
        Map<Integer, String> artistNames = artistRepo.findAll().stream()
                .collect(Collectors.toMap(ArtistAccount::getArtistId, ArtistAccount::getStageName, (a, b) -> a));

        // Also need to link songs to artists for the artist name in history
        Map<Integer, Integer> songToArtist = songRepo.findAll().stream()
                .collect(Collectors.toMap(Song::getSongId, Song::getArtistId, (a, b) -> a));

        for (ListeningHistory h : history) {
            h.setSongTitle(songTitles.getOrDefault(h.getSongId(), "Unknown Song"));
            Integer artistId = songToArtist.get(h.getSongId());
            if (artistId != null) {
                h.setArtistName(artistNames.getOrDefault(artistId, "Unknown Artist"));
            }
        }
    }

    public boolean clearHistory(int userId) {
        try {
            historyRepo.deleteByUserId(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
