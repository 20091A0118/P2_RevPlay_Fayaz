package com.revplay.app.repository;

import com.revplay.app.entity.ArtistAccount;
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
public class ArtistRepoImpl implements IArtistRepo {

    private static final Logger logger = LogManager.getLogger(ArtistRepoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<ArtistAccount> rowMapper = (rs, rowNum) -> {
        ArtistAccount artist = new ArtistAccount();
        artist.setArtistId(rs.getInt("artist_id"));
        artist.setStageName(rs.getString("stage_name"));
        artist.setEmail(rs.getString("email"));
        artist.setPasswordHash(rs.getString("password_hash"));
        artist.setBio(rs.getString("bio"));
        artist.setGenre(rs.getString("genre"));
        artist.setInstagramLink(rs.getString("instagram_link"));
        artist.setYoutubeLink(rs.getString("youtube_link"));
        artist.setSpotifyLink(rs.getString("spotify_link"));
        artist.setStatus(rs.getString("status"));
        artist.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return artist;
    };

    public boolean registerArtist(ArtistAccount artist) {
        String sql = "INSERT INTO ARTIST_ACCOUNT (stage_name, email, password_hash, bio, genre, instagram_link, youtube_link, spotify_link, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            int rows = jdbcTemplate.update(sql,
                    artist.getStageName(), artist.getEmail(), artist.getPasswordHash(),
                    artist.getBio(), artist.getGenre(),
                    artist.getInstagramLink(), artist.getYoutubeLink(),
                    artist.getSpotifyLink(), artist.getStatus(),
                    java.sql.Timestamp.valueOf(artist.getCreatedAt()));
            return rows > 0;
        } catch (DataAccessException e) {
            logger.error("Error in registerArtist: {}", e.getMessage());
            return false;
        }
    }

    public ArtistAccount getArtistByEmail(String email) {
        String sql = "SELECT * FROM ARTIST_ACCOUNT WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public ArtistAccount getArtistById(int id) {
        String sql = "SELECT * FROM ARTIST_ACCOUNT WHERE artist_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean updateArtistProfile(ArtistAccount artist) {
        String sql = "UPDATE ARTIST_ACCOUNT SET stage_name = ?, password_hash = ?, bio = ?, genre = ?, instagram_link = ?, youtube_link = ?, spotify_link = ? WHERE artist_id = ?";
        try {
            int rows = jdbcTemplate.update(sql,
                    artist.getStageName(), artist.getPasswordHash(),
                    artist.getBio(), artist.getGenre(),
                    artist.getInstagramLink(), artist.getYoutubeLink(),
                    artist.getSpotifyLink(), artist.getArtistId());
            return rows > 0;
        } catch (DataAccessException e) {
            logger.error("Error in updateArtistProfile: {}", e.getMessage());
            return false;
        }
    }

    public List<ArtistAccount> getAllArtists() {
        String sql = "SELECT * FROM ARTIST_ACCOUNT";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<ArtistAccount> searchArtists(String keyword) {
        String sql = "SELECT * FROM ARTIST_ACCOUNT WHERE LOWER(stage_name) LIKE LOWER(?) OR LOWER(genre) LIKE LOWER(?)";
        String pattern = "%" + keyword + "%";
        return jdbcTemplate.query(sql, rowMapper, pattern, pattern);
    }

    public int getTotalPlays(int artistId) {
        String sql = "SELECT NVL(SUM(play_count), 0) FROM SONG WHERE artist_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, artistId);
        return count != null ? count : 0;
    }

    public int getTotalFavorites(int artistId) {
        String sql = "SELECT COUNT(*) FROM FAVORITE_SONG fs JOIN SONG s ON fs.song_id = s.song_id WHERE s.artist_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, artistId);
        return count != null ? count : 0;
    }
}
