package com.revplay.app.repository;

import com.revplay.app.entity.Podcast;
import com.revplay.app.entity.PodcastEpisode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PodcastRepoImpl implements IPodcastRepo {

    private static final Logger logger = LogManager.getLogger(PodcastRepoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Podcast> podcastRowMapper = (rs, rowNum) -> {
        Podcast p = new Podcast(rs.getInt("podcast_id"),
                rs.getString("title"),
                rs.getString("host_name"), rs.getString("category"),
                rs.getString("description"), rs.getTimestamp("created_at").toLocalDateTime());
        try {
            p.setCoverImageUrl(rs.getString("cover_image_url"));
        } catch (Exception e) {
        }
        return p;
    };

    private final RowMapper<PodcastEpisode> episodeRowMapper = (rs, rowNum) -> new PodcastEpisode(
            rs.getInt("episode_id"), rs.getInt("podcast_id"),
            rs.getString("title"), rs.getInt("duration_seconds"),
            rs.getDate("release_date").toLocalDate(), rs.getString("file_url"),
            rs.getInt("play_count"), rs.getTimestamp("created_at").toLocalDateTime());

    public boolean createPodcast(Podcast podcast) {
        try {
            return jdbcTemplate.update(
                    "INSERT INTO PODCAST (title, host_name, category, description, cover_image_url, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                    podcast.getTitle(), podcast.getHostName(), podcast.getCategory(),
                    podcast.getDescription(), podcast.getCoverImageUrl(), Timestamp.valueOf(LocalDateTime.now())) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in createPodcast: {}", e.getMessage());
            return false;
        }
    }

    public List<Podcast> getAllPodcasts() {
        return jdbcTemplate.query("SELECT * FROM PODCAST ORDER BY created_at DESC", podcastRowMapper);
    }

    public Podcast getPodcastById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM PODCAST WHERE podcast_id = ?", podcastRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean addEpisode(PodcastEpisode episode) {
        try {
            return jdbcTemplate.update(
                    "INSERT INTO PODCAST_EPISODE (podcast_id, title, duration_seconds, release_date, file_url, play_count, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    episode.getPodcastId(), episode.getTitle(), episode.getDurationSeconds(),
                    java.sql.Date.valueOf(episode.getReleaseDate()), episode.getFileUrl(),
                    0, Timestamp.valueOf(LocalDateTime.now())) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in addEpisode: {}", e.getMessage());
            return false;
        }
    }

    public List<PodcastEpisode> getEpisodesByPodcastId(int podcastId) {
        return jdbcTemplate.query("SELECT * FROM PODCAST_EPISODE WHERE podcast_id = ? ORDER BY release_date DESC",
                episodeRowMapper, podcastId);
    }

    public boolean incrementEpisodePlayCount(int episodeId) {
        try {
            return jdbcTemplate.update("UPDATE PODCAST_EPISODE SET play_count = play_count + 1 WHERE episode_id = ?",
                    episodeId) > 0;
        } catch (DataAccessException e) {
            logger.error("Error in incrementEpisodePlayCount: {}", e.getMessage());
            return false;
        }
    }
}
