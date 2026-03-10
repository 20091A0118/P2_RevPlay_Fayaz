package com.revplay.app.service;

import com.revplay.app.entity.ArtistAccount;
import com.revplay.app.entity.Song;
import com.revplay.app.repository.IArtistRepository;
import com.revplay.app.repository.IListeningHistoryRepository;
import com.revplay.app.entity.ListeningHistory;
import com.revplay.app.repository.ISongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class HistoryServiceImpl implements IHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(HistoryServiceImpl.class);

    @Autowired
    private IListeningHistoryRepository historyRepository;

    @Autowired
    private ISongRepository songRepository;

    @Autowired
    private IArtistRepository artistRepository;

    public List<ListeningHistory> getUserHistory(int userId) {
        logger.info("getUserHistory method called in Service for userId: {}", userId);
        List<ListeningHistory> history = historyRepository.findByUserIdOrderByPlayedAtDesc(userId);
        enrichHistory(history);
        return history;
    }

    public void addHistory(int userId, int songId) {
        logger.info("addHistory method called in Service for userId: {}, songId: {}", userId, songId);
        ListeningHistory history = new ListeningHistory();
        history.setUserId(userId);
        history.setSongId(songId);
        history.setPlayedAt(LocalDateTime.now());
        history.setActionType("PLAYED");
        historyRepository.save(history);
    }

    private void enrichHistory(List<ListeningHistory> history) {
        if (history == null || history.isEmpty())
            return;
        Map<Integer, String> songTitles = songRepository.findAll().stream()
                .collect(Collectors.toMap(Song::getSongId, Song::getTitle, (a, b) -> a));
        Map<Integer, String> artistNames = artistRepository.findAll().stream()
                .collect(Collectors.toMap(ArtistAccount::getArtistId,
                        artist -> artist.getStageName() != null ? artist.getStageName() : "Unknown Artist",
                        (a, b) -> a));

        // Also need to link songs to artists for the artist name in history
        Map<Integer, Integer> songToArtist = songRepository.findAll().stream()
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
        logger.info("clearHistory method called in Service for userId: {}", userId);
        try {
            historyRepository.deleteByUserId(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
