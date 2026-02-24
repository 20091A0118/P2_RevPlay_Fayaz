package com.revplay.app.service;

import com.revplay.app.entity.Song;
import com.revplay.app.repository.ISongRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongServiceImpl implements ISongService {

    @Autowired
    private ISongRepo songRepo;

    @Override
    public boolean addSong(Song song) {
        return songRepo.addSong(song);
    }

    @Override
    public Song getSongById(int songId) {
        return songRepo.getSongById(songId);
    }

    @Override
    public List<Song> getAllSongs() {
        return songRepo.getAllSongs();
    }

    @Override
    public List<Song> getSongsByArtistId(int artistId) {
        return songRepo.getSongsByArtistId(artistId);
    }

    @Override
    public List<Song> getSongsByAlbumId(int albumId) {
        return songRepo.getSongsByAlbumId(albumId);
    }

    @Override
    public List<Song> getSongsByGenreId(int genreId) {
        return songRepo.getSongsByGenreId(genreId);
    }

    @Override
    public List<Song> searchSongs(String keyword) {
        return songRepo.searchSongs(keyword);
    }

    @Override
    public boolean updateSong(Song song) {
        return songRepo.updateSong(song);
    }

    @Override
    public boolean deleteSong(int songId) {
        return songRepo.deleteSong(songId);
    }

    @Override
    public boolean incrementPlayCount(int songId) {
        return songRepo.incrementPlayCount(songId);
    }

    @Override
    public List<Song> getTopPlayedSongs(int limit) {
        return songRepo.getTopPlayedSongs(limit);
    }

    @Override
    public int countSongsByArtist(int artistId) {
        return songRepo.countSongsByArtist(artistId);
    }
}
