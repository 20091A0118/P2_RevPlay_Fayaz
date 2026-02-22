package com.revplay.app.repository;

import com.revplay.app.entity.Song;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FavoriteRepoImpl implements IFavoriteRepo {

    private static final Logger logger = LogManager.getLogger(FavoriteRepoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Song> songRowMapper = (rs, rowNum) -> {
        Song song = new Song();
        song.setSongId(rs.getInt("song_id"));
        song.setTitle(rs.getString("title"));
        song.setAlbumId(rs.getInt("album_id"));
        if (rs.wasNull())
            song.setAlbumId(null);
        song.setArtistId(rs.getInt("artist_id"));
        song.setGenreId(rs.getInt("genre_id"));
        song.setDurationSeconds(rs.getInt("duration_seconds"));
        song.setReleaseDate(rs.getDate("release_date").toLocalDate());
        song.setPlayCount(rs.getInt("play_count"));
        song.setIsActive(rs.getString("is_active"));
        song.setFileUrl(rs.getString("file_url"));
        try {
            song.setGenreName(rs.getString("genre_name"));
        } catch (Exception e) {
        }
        try {
            song.setArtistName(rs.getString("stage_name"));
        } catch (Exception e) {
        }
        return song;
    };

    public boolean addFavorite(int userId, int songId) {
        String sql = "INSERT INTO FAVORITE_SONG (user_id, song_id, favorited_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try {
            return jdbcTemplate.update(sql, userId, songId) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in addFavorite: {}", e.getMessage());
            return false;
        }
    }

    public boolean removeFavorite(int userId, int songId) {
        try {
            return jdbcTemplate.update("DELETE FROM FAVORITE_SONG WHERE user_id = ? AND song_id = ?",
                    userId, songId) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in removeFavorite: {}", e.getMessage());
            return false;
        }
    }

    public List<Song> getFavoriteSongs(int userId) {
        String sql = "SELECT s.*, g.genre_name, a.stage_name FROM SONG s " +
                "JOIN FAVORITE_SONG f ON s.song_id = f.song_id " +
                "LEFT JOIN GENRE g ON s.genre_id = g.genre_id " +
                "LEFT JOIN ARTIST_ACCOUNT a ON s.artist_id = a.artist_id " +
                "WHERE f.user_id = ? ORDER BY f.favorited_at DESC";
        return jdbcTemplate.query(sql, songRowMapper, userId);
    }

    public boolean isFavorite(int userId, int songId) {
        String sql = "SELECT COUNT(*) FROM FAVORITE_SONG WHERE user_id = ? AND song_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, songId);
        return count != null && count > 0;
    }
}
