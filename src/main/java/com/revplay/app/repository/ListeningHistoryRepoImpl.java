package com.revplay.app.repository;

import com.revplay.app.entity.ListeningHistory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ListeningHistoryRepoImpl implements IListeningHistoryRepo {

    private static final Logger logger = LogManager.getLogger(ListeningHistoryRepoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<ListeningHistory> rowMapper = (rs, rowNum) -> {
        ListeningHistory h = new ListeningHistory();
        h.setHistoryId(rs.getInt("history_id"));
        h.setUserId(rs.getInt("user_id"));
        h.setSongId(rs.getInt("song_id"));
        h.setPlayedAt(rs.getTimestamp("played_at").toLocalDateTime());
        h.setActionType(rs.getString("action_type"));
        try {
            h.setSongTitle(rs.getString("title"));
        } catch (Exception e) {
        }
        try {
            h.setArtistName(rs.getString("stage_name"));
        } catch (Exception e) {
        }
        return h;
    };

    public boolean addHistory(int userId, int songId) {
        String sql = "INSERT INTO LISTENING_HISTORY (user_id, song_id, played_at, action_type) VALUES (?, ?, ?, ?)";
        try {
            return jdbcTemplate.update(sql, userId, songId,
                    Timestamp.valueOf(LocalDateTime.now()), "PLAYED") > 0;
        } catch (DataAccessException e) {
            logger.error("Error in addHistory: {}", e.getMessage());
            return false;
        }
    }

    public List<ListeningHistory> getUserHistory(int userId) {
        String sql = "SELECT h.*, s.title, a.stage_name FROM LISTENING_HISTORY h " +
                "JOIN SONG s ON h.song_id = s.song_id " +
                "LEFT JOIN ARTIST_ACCOUNT a ON s.artist_id = a.artist_id " +
                "WHERE h.user_id = ? ORDER BY h.played_at DESC FETCH FIRST 50 ROWS ONLY";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public boolean clearHistory(int userId) {
        try {
            jdbcTemplate.update("DELETE FROM LISTENING_HISTORY WHERE user_id = ?", userId);
            return true;
        } catch (DataAccessException e) {
            logger.error("Error in clearHistory: {}", e.getMessage());
            return false;
        }
    }
}
