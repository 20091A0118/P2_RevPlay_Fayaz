package com.revplay.app.service;

import com.revplay.app.entity.Playlist;
import com.revplay.app.entity.Song;
import com.revplay.app.repository.IPlaylistRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistServiceImpl implements IPlaylistService {

    @Autowired
    private IPlaylistRepo playlistRepo;

    @Override
    public boolean createPlaylist(Playlist playlist) {
        return playlistRepo.createPlaylist(playlist);
    }

    @Override
    public boolean deletePlaylist(int playlistId) {
        return playlistRepo.deletePlaylist(playlistId);
    }

    @Override
    public boolean updatePlaylist(Playlist playlist) {
        return playlistRepo.updatePlaylist(playlist);
    }

    @Override
    public boolean addSongToPlaylist(int playlistId, int songId) {
        return playlistRepo.addSongToPlaylist(playlistId, songId);
    }

    @Override
    public boolean removeSongFromPlaylist(int playlistId, int songId) {
        return playlistRepo.removeSongFromPlaylist(playlistId, songId);
    }

    @Override
    public List<Playlist> getPlaylistsByUserId(int userId) {
        return playlistRepo.getPlaylistsByUserId(userId);
    }

    @Override
    public List<Playlist> getPublicPlaylists() {
        return playlistRepo.getPublicPlaylists();
    }

    @Override
    public Playlist getPlaylistById(int playlistId) {
        return playlistRepo.getPlaylistById(playlistId);
    }

    @Override
    public List<Song> getSongsInPlaylist(int playlistId) {
        return playlistRepo.getSongsInPlaylist(playlistId);
    }
}
