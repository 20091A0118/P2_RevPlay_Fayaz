package com.revplay.app.repository;

import com.revplay.app.entity.Song;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SongRepoImpl implements ISongRepo {

    private static final Logger logger = LogManager.getLogger(SongRepoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Song> rowMapper = (rs, rowNum) -> {
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
            song.setCoverImageUrl(rs.getString("cover_image_url"));
        } catch (Exception e) {
        }
        try {
            song.setGenreName(rs.getString("genre_name"));
        } catch (Exception e) {
        }
        try {
            song.setArtistName(rs.getString("stage_name"));
        } catch (Exception e) {
        }
        try {
            song.setAlbumTitle(rs.getString("album_title"));
        } catch (Exception e) {
        }
        return song;
    };

    private static final String SELECT_SONGS = "SELECT s.*, g.genre_name, a.stage_name, al.title as album_title " +
            "FROM SONG s LEFT JOIN GENRE g ON s.genre_id = g.genre_id " +
            "LEFT JOIN ARTIST_ACCOUNT a ON s.artist_id = a.artist_id " +
            "LEFT JOIN ALBUM al ON s.album_id = al.album_id";

    public boolean addSong(Song song) {
        String sql = "INSERT INTO SONG (title, album_id, artist_id, genre_id, duration_seconds, release_date, play_count, is_active, file_url, cover_image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            int rows = jdbcTemplate.update(sql,
                    song.getTitle(),
                    song.getAlbumId() != null && song.getAlbumId() > 0 ? song.getAlbumId() : null,
                    song.getArtistId(), song.getGenreId(),
                    song.getDurationSeconds(),
                    java.sql.Date.valueOf(song.getReleaseDate()),
                    song.getPlayCount(), "ACTIVE", song.getFileUrl(), song.getCoverImageUrl());
            return rows > 0;
        } catch (DataAccessException e) {
            logger.error("Error in addSong: {}", e.getMessage());
            return false;
        }
    }

    public Song getSongById(int songId) {
        String sql = SELECT_SONGS + " WHERE s.song_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, songId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Song> getAllSongs() {
        return jdbcTemplate.query(SELECT_SONGS + " ORDER BY s.created_at DESC", rowMapper);
    }

    public List<Song> getSongsByArtistId(int artistId) {
        String sql = SELECT_SONGS + " WHERE s.artist_id = ?";
        return jdbcTemplate.query(sql, rowMapper, artistId);
    }

    public List<Song> getSongsByAlbumId(int albumId) {
        String sql = SELECT_SONGS + " WHERE s.album_id = ?";
        return jdbcTemplate.query(sql, rowMapper, albumId);
    }

    public List<Song> getSongsByGenreId(int genreId) {
        String sql = SELECT_SONGS + " WHERE s.genre_id = ?";
        return jdbcTemplate.query(sql, rowMapper, genreId);
    }

    public List<Song> searchSongs(String keyword) {
        String sql = SELECT_SONGS
                + " WHERE LOWER(s.title) LIKE LOWER(?) OR LOWER(g.genre_name) LIKE LOWER(?) OR LOWER(a.stage_name) LIKE LOWER(?)";
        String pattern = "%" + keyword + "%";
        return jdbcTemplate.query(sql, rowMapper, pattern, pattern, pattern);
    }

    public boolean updateSong(Song song) {
        String sql = "UPDATE SONG SET title = ?, genre_id = ?, album_id = ?, is_active = ? WHERE song_id = ?";
        try {
            int rows = jdbcTemplate.update(sql, song.getTitle(), song.getGenreId(),
                    song.getAlbumId(), song.getIsActive(), song.getSongId());
            return rows > 0;
        } catch (DataAccessException e) {
            logger.error("Error in updateSong: {}", e.getMessage());
            return false;
        }
    }

    public boolean deleteSong(int songId) {
        String sql = "DELETE FROM SONG WHERE song_id = ?";
        try {
            return jdbcTemplate.update(sql, songId) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in deleteSong: {}", e.getMessage());
            return false;
        }
    }

    public boolean incrementPlayCount(int songId) {
        String sql = "UPDATE SONG SET play_count = play_count + 1 WHERE song_id = ?";
        try {
            return jdbcTemplate.update(sql, songId) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in incrementPlayCount: {}", e.getMessage());
            return false;
        }
    }

    public List<Song> getTopPlayedSongs(int limit) {
        String sql = SELECT_SONGS + " ORDER BY s.play_count DESC FETCH FIRST ? ROWS ONLY";
        return jdbcTemplate.query(sql, rowMapper, limit);
    }

    public int countSongsByArtist(int artistId) {
        String sql = "SELECT COUNT(*) FROM SONG WHERE artist_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, artistId);
        return count != null ? count : 0;
    }
}
