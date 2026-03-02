package com.revplay.app.service;

import com.revplay.app.entity.Playlist;
import com.revplay.app.entity.Song;
import java.util.List;

public interface IPlaylistService {
    boolean createPlaylist(Playlist playlist);

    boolean deletePlaylist(int playlistId);

    boolean updatePlaylist(Playlist playlist);

    boolean addSongToPlaylist(int playlistId, int songId);

    boolean removeSongFromPlaylist(int playlistId, int songId);

    List<Playlist> getUserPlaylists(int userId);

    Playlist getPlaylistById(int playlistId);

    List<Song> getPlaylistSongs(int playlistId);

    List<Playlist> getPublicPlaylists();

    boolean reorderPlaylistSongs(int playlistId, List<Integer> newSongIds);

    boolean followPlaylist(int userId, int playlistId);

    boolean unfollowPlaylist(int userId, int playlistId);

    List<Playlist> getFollowedPlaylists(int userId);
}
