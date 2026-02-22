package com.revplay.app.repository;

import com.revplay.app.entity.Playlist;
import com.revplay.app.entity.Song;
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
public class PlaylistRepoImpl implements IPlaylistRepo {

    private static final Logger logger = LogManager.getLogger(PlaylistRepoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Playlist> playlistRowMapper = (rs, rowNum) -> {
        Playlist p = new Playlist();
        p.setPlaylistId(rs.getInt("playlist_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrivacyStatus(rs.getString("privacy_status"));
        p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        p.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return p;
    };

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

    public boolean createPlaylist(Playlist playlist) {
        String sql = "INSERT INTO PLAYLIST (user_id, name, description, privacy_status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            return jdbcTemplate.update(sql, playlist.getUserId(), playlist.getName(),
                    playlist.getDescription(), playlist.getPrivacyStatus(), now, now) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in createPlaylist: {}", e.getMessage());
            return false;
        }
    }

    public boolean deletePlaylist(int playlistId) {
        try {
            jdbcTemplate.update("DELETE FROM PLAYLIST_SONG WHERE playlist_id = ?", playlistId);
            return jdbcTemplate.update("DELETE FROM PLAYLIST WHERE playlist_id = ?", playlistId) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in deletePlaylist: {}", e.getMessage());
            return false;
        }
    }

    public boolean updatePlaylist(Playlist playlist) {
        String sql = "UPDATE PLAYLIST SET name = ?, description = ?, privacy_status = ?, updated_at = ? WHERE playlist_id = ?";
        try {
            return jdbcTemplate.update(sql, playlist.getName(), playlist.getDescription(),
                    playlist.getPrivacyStatus(), Timestamp.valueOf(LocalDateTime.now()),
                    playlist.getPlaylistId()) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in updatePlaylist: {}", e.getMessage());
            return false;
        }
    }

    public boolean addSongToPlaylist(int playlistId, int songId) {
        String sql = "INSERT INTO PLAYLIST_SONG (playlist_id, song_id, added_at) VALUES (?, ?, ?)";
        try {
            int rows = jdbcTemplate.update(sql, playlistId, songId, Timestamp.valueOf(LocalDateTime.now()));
            if (rows > 0) {
                jdbcTemplate.update("UPDATE PLAYLIST SET updated_at = ? WHERE playlist_id = ?",
                        Timestamp.valueOf(LocalDateTime.now()), playlistId);
            }
            return rows > 0;
        } catch (DataAccessException e) {
            logger.error("Error in addSongToPlaylist: {}", e.getMessage());
            return false;
        }
    }

    public boolean removeSongFromPlaylist(int playlistId, int songId) {
        try {
            int rows = jdbcTemplate.update("DELETE FROM PLAYLIST_SONG WHERE playlist_id = ? AND song_id = ?",
                    playlistId, songId);
            if (rows > 0) {
                jdbcTemplate.update("UPDATE PLAYLIST SET updated_at = ? WHERE playlist_id = ?",
                        Timestamp.valueOf(LocalDateTime.now()), playlistId);
            }
            return rows > 0;
        } catch (DataAccessException e) {
            logger.error("Error in removeSongFromPlaylist: {}", e.getMessage());
            return false;
        }
    }

    public List<Playlist> getPlaylistsByUserId(int userId) {
        return jdbcTemplate.query("SELECT * FROM PLAYLIST WHERE user_id = ? ORDER BY updated_at DESC",
                playlistRowMapper, userId);
    }

    public List<Playlist> getPublicPlaylists() {
        return jdbcTemplate.query("SELECT * FROM PLAYLIST WHERE privacy_status = 'PUBLIC' ORDER BY updated_at DESC",
                playlistRowMapper);
    }

    public Playlist getPlaylistById(int playlistId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM PLAYLIST WHERE playlist_id = ?",
                    playlistRowMapper, playlistId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Song> getSongsInPlaylist(int playlistId) {
        String sql = "SELECT s.*, g.genre_name, a.stage_name FROM SONG s " +
                "JOIN PLAYLIST_SONG ps ON s.song_id = ps.song_id " +
                "LEFT JOIN GENRE g ON s.genre_id = g.genre_id " +
                "LEFT JOIN ARTIST_ACCOUNT a ON s.artist_id = a.artist_id " +
                "WHERE ps.playlist_id = ? ORDER BY ps.added_at";
        return jdbcTemplate.query(sql, songRowMapper, playlistId);
    }
}
