package com.revplay.app.service;

import com.revplay.app.entity.Song;

import java.util.List;

public interface ISongService {
    boolean addSong(Song song);

    Song getSongById(int songId);

    List<Song> getAllSongs();

    List<Song> getSongsByArtistId(int artistId);

    List<Song> getSongsByAlbumId(int albumId);

    List<Song> getSongsByGenreId(int genreId);

    List<Song> searchSongs(String keyword);

    boolean updateSong(Song song);

    boolean deleteSong(int songId);

    boolean incrementPlayCount(int songId);

    List<Song> getTopPlayedSongs(int limit);

    int countSongsByArtist(int artistId);
}
