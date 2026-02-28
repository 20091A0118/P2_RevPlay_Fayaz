package com.revplay.app.service;

import com.revplay.app.repository.IPlaylistRepo;
import com.revplay.app.repository.ISongRepo;
import com.revplay.app.entity.Playlist;
import com.revplay.app.entity.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistServiceImpl implements IPlaylistService {

    @Autowired
    private IPlaylistRepo playlistDao;

    @Autowired
    private ISongRepo songRepo;

    @Autowired
    private ISongService songService;

    public void setPlaylistDao(IPlaylistRepo playlistDao) {
        this.playlistDao = playlistDao;
    }

    public void setSongRepo(ISongRepo songRepo) {
        this.songRepo = songRepo;
    }

    public boolean createPlaylist(Playlist playlist) {
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
        try {
            playlist.setUpdatedAt(java.time.LocalDateTime.now());
            playlistDao.save(playlist);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addSongToPlaylist(int playlistId, int songId) {
        return playlistDao.findById(playlistId).map(playlist -> {
            return songRepo.findById(songId).map(song -> {
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
        return playlistDao.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    public Playlist getPlaylistById(int playlistId) {
        return playlistDao.findById(playlistId).orElse(null);
    }

    public List<Song> getPlaylistSongs(int playlistId) {
        List<Song> songs = playlistDao.findById(playlistId)
                .map(Playlist::getSongs)
                .orElse(java.util.Collections.emptyList());
        songService.enrichSongs(songs);
        return songs;
    }

    public List<Playlist> getPublicPlaylists() {
        return playlistDao.findByPrivacyStatusOrderByUpdatedAtDesc("PUBLIC");
    }
}
