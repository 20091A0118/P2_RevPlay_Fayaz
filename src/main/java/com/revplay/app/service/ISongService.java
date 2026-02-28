package com.revplay.app.service;

import com.revplay.app.entity.Song;
import java.util.List;

public interface ISongService {
    boolean uploadSong(Song song);

    List<Song> getAllSongs();

    Song getSongById(int songId);

    List<Song> getSongsByArtist(int artistId);

    List<Song> getSongsByAlbum(int albumId);

    List<Song> getSongsByGenre(int genreId);

    List<Song> searchSongs(String keyword);

    void playSong(int songId, Integer userId);

    boolean updateSong(Song song);

    boolean deleteSong(int songId);

    void enrichSongs(List<Song> songs);

    List<Song> getTopPlayedSongs(int limit);

    int countSongsByArtist(int artistId);
}
