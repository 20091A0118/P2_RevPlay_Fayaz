package com.revplay.app.repository;

import com.revplay.app.entity.Playlist;
import com.revplay.app.entity.Song;
import java.util.List;

public interface IPlaylistRepo {
    boolean createPlaylist(Playlist playlist);

    boolean deletePlaylist(int playlistId);

    boolean updatePlaylist(Playlist playlist);

    boolean addSongToPlaylist(int playlistId, int songId);

    boolean removeSongFromPlaylist(int playlistId, int songId);

    List<Playlist> getPlaylistsByUserId(int userId);

    List<Playlist> getPublicPlaylists();

    Playlist getPlaylistById(int playlistId);

    List<Song> getSongsInPlaylist(int playlistId);
}
