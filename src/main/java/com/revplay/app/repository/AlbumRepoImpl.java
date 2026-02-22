package com.revplay.app.repository;

import com.revplay.app.entity.Album;
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
public class AlbumRepoImpl implements IAlbumRepo {

    private static final Logger logger = LogManager.getLogger(AlbumRepoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Album> rowMapper = (rs, rowNum) -> {
        Album album = new Album();
        album.setAlbumId(rs.getInt("album_id"));
        album.setArtistId(rs.getInt("artist_id"));
        album.setTitle(rs.getString("title"));
        album.setReleaseDate(rs.getDate("release_date").toLocalDate());
        album.setDescription(rs.getString("description"));
        album.setCoverImageUrl(rs.getString("cover_image_url"));
        try {
            album.setArtistName(rs.getString("stage_name"));
        } catch (Exception e) {
        }
        return album;
    };

    private static final String SELECT_ALBUMS = "SELECT al.*, a.stage_name FROM ALBUM al LEFT JOIN ARTIST_ACCOUNT a ON al.artist_id = a.artist_id";

    public boolean createAlbum(Album album) {
        String sql = "INSERT INTO ALBUM (artist_id, title, release_date, description, cover_image_url) VALUES (?, ?, ?, ?, ?)";
        try {
            return jdbcTemplate.update(sql, album.getArtistId(), album.getTitle(),
                    java.sql.Date.valueOf(album.getReleaseDate()),
                    album.getDescription(), album.getCoverImageUrl()) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in createAlbum: {}", e.getMessage());
            return false;
        }
    }

    public Album getAlbumById(int albumId) {
        String sql = SELECT_ALBUMS + " WHERE al.album_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, albumId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Album> getAlbumsByArtistId(int artistId) {
        String sql = SELECT_ALBUMS + " WHERE al.artist_id = ?";
        return jdbcTemplate.query(sql, rowMapper, artistId);
    }

    public List<Album> getAllAlbums() {
        return jdbcTemplate.query(SELECT_ALBUMS, rowMapper);
    }

    public boolean updateAlbum(Album album) {
        String sql = "UPDATE ALBUM SET title = ?, description = ?, cover_image_url = ?, release_date = ? WHERE album_id = ?";
        try {
            return jdbcTemplate.update(sql, album.getTitle(), album.getDescription(),
                    album.getCoverImageUrl(), java.sql.Date.valueOf(album.getReleaseDate()),
                    album.getAlbumId()) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in updateAlbum: {}", e.getMessage());
            return false;
        }
    }

    public boolean deleteAlbum(int albumId) {
        try {
            return jdbcTemplate.update("DELETE FROM ALBUM WHERE album_id = ?", albumId) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in deleteAlbum: {}", e.getMessage());
            return false;
        }
    }
}
