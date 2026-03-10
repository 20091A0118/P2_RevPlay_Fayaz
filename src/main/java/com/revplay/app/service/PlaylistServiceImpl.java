package com.revplay.app.service;

import com.revplay.app.repository.IPlaylistRepository;
import com.revplay.app.repository.ISongRepository;
import com.revplay.app.entity.Playlist;
import com.revplay.app.entity.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PlaylistServiceImpl implements IPlaylistService {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistServiceImpl.class);

    @Autowired
    private IPlaylistRepository playlistDao;

    @Autowired
    private ISongRepository songRepository;

    @Autowired
    private ISongService songService;

    public void setPlaylistDao(IPlaylistRepository playlistDao) {
        this.playlistDao = playlistDao;
    }

    public void setSongRepository(ISongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public boolean createPlaylist(Playlist playlist) {
        logger.info("createPlaylist method called in Service");
        try {
            playlist.setCreatedAt(java.time.LocalDateTime.now());
            playlist.setUpdatedAt(java.time.LocalDateTime.now());
            playlistDao.save(playlist);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deletePlaylist(int playlistId) {
        logger.info("deletePlaylist method called in Service for playlistId: {}", playlistId);
        try {
            if (!playlistDao.existsById(playlistId)) {
                return false;
            }
            playlistDao.deleteById(playlistId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updatePlaylist(Playlist playlist) {
        logger.info("updatePlaylist method called in Service for playlistId: {}", playlist.getPlaylistId());
        try {
            return playlistDao.findById(playlist.getPlaylistId()).map(existing -> {
                if (playlist.getName() != null)
                    existing.setName(playlist.getName());
                if (playlist.getDescription() != null)
                    existing.setDescription(playlist.getDescription());
                if (playlist.getPrivacyStatus() != null)
                    existing.setPrivacyStatus(playlist.getPrivacyStatus());
                existing.setUpdatedAt(java.time.LocalDateTime.now());
                playlistDao.save(existing);
                return true;
            }).orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addSongToPlaylist(int playlistId, int songId) {
        logger.info("addSongToPlaylist method called in Service for playlistId: {}, songId: {}", playlistId, songId);
        return playlistDao.findById(playlistId).map(playlist -> {
            return songRepository.findById(songId).map(song -> {
                if (!playlist.getSongs().contains(song)) {
                    playlist.getSongs().add(song);
                    playlist.setUpdatedAt(java.time.LocalDateTime.now());
                    playlistDao.save(playlist);
                }
                return true;
            }).orElse(false);
        }).orElse(false);
    }

    public boolean removeSongFromPlaylist(int playlistId, int songId) {
        logger.info("removeSongFromPlaylist method called in Service for playlistId: {}, songId: {}", playlistId,
                songId);
        return playlistDao.findById(playlistId).map(playlist -> {
            boolean removed = playlist.getSongs().removeIf(s -> s.getSongId() == songId);
            if (removed) {
                playlist.setUpdatedAt(java.time.LocalDateTime.now());
                playlistDao.save(playlist);
            }
            return removed;
        }).orElse(false);
    }

    public List<Playlist> getUserPlaylists(int userId) {
        logger.info("getUserPlaylists method called in Service for userId: {}", userId);
        return playlistDao.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    public Playlist getPlaylistById(int playlistId) {
        logger.info("getPlaylistById method called in Service for playlistId: {}", playlistId);
        return playlistDao.findById(playlistId).orElse(null);
    }

    public List<Song> getPlaylistSongs(int playlistId) {
        logger.info("getPlaylistSongs method called in Service for playlistId: {}", playlistId);
        List<Song> songs = playlistDao.findById(playlistId)
                .map(Playlist::getSongs)
                .orElse(java.util.Collections.emptyList());
        songService.enrichSongs(songs);
        return songs;
    }

    public List<Playlist> getPublicPlaylists() {
        logger.info("getPublicPlaylists method called in Service");
        return playlistDao.findByPrivacyStatusOrderByUpdatedAtDesc("PUBLIC");
    }
}
