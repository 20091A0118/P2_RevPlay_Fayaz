package com.revplay.app.service;

import com.revplay.app.repository.ISongRepo;
import com.revplay.app.repository.IArtistRepo;
import com.revplay.app.repository.IGenreRepo;
import com.revplay.app.entity.Song;
import com.revplay.app.entity.ArtistAccount;
import com.revplay.app.entity.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revplay.app.service.IHistoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl implements ISongService {

    @Autowired
    private ISongRepo songDao;

    @Autowired
    private IHistoryService historyService;

    @Autowired
    private IArtistRepo artistRepo;

    @Autowired
    private IGenreRepo genreRepo;

    public void setSongDao(ISongRepo songDao) {
        this.songDao = songDao;
    }

    // Populate transient artistName/genreName on a list of songs
    public void enrichSongs(List<Song> songs) {
        if (songs == null || songs.isEmpty())
            return;
        Map<Integer, String> artistNames = artistRepo.findAll().stream()
                .collect(Collectors.toMap(ArtistAccount::getArtistId, ArtistAccount::getStageName, (a, b) -> a));
        Map<Integer, String> genreNames = genreRepo.findAll().stream()
                .collect(Collectors.toMap(Genre::getGenreId, Genre::getGenreName, (a, b) -> a));
        for (Song s : songs) {
            s.setArtistName(artistNames.getOrDefault(s.getArtistId(), "Unknown"));
            s.setGenreName(genreNames.getOrDefault(s.getGenreId(), "-"));
        }
    }

    public boolean uploadSong(Song song) {
        try {
            if (song.getIsActive() == null)
                song.setIsActive("Y");
            if (song.getCreatedAt() == null)
                song.setCreatedAt(LocalDateTime.now());
            songDao.save(song);
            return true;
        } catch (Exception e) {
            System.err.println("Song upload failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Song> getAllSongs() {
        List<Song> songs = songDao.findAll();
        enrichSongs(songs);
        return songs;
    }

    public Song getSongById(int songId) {
        return songDao.findById(songId).orElse(null);
    }

    public List<Song> getSongsByArtist(int artistId) {
        List<Song> songs = songDao.findByArtistId(artistId);
        enrichSongs(songs);
        return songs;
    }

    public List<Song> getSongsByAlbum(int albumId) {
        List<Song> songs = songDao.findByAlbumId(albumId);
        enrichSongs(songs);
        return songs;
    }

    public List<Song> getSongsByGenre(int genreId) {
        List<Song> songs = songDao.findByGenreId(genreId);
        enrichSongs(songs);
        return songs;
    }

    public List<Song> searchSongs(String keyword) {
        List<Song> songs = songDao.searchSongs(keyword);
        enrichSongs(songs);
        return songs;
    }

    public void playSong(int songId, Integer userId) {
        songDao.findById(songId).ifPresent(song -> {
            song.setPlayCount(song.getPlayCount() + 1);
            songDao.save(song);
        });
        if (userId != null) {
            historyService.addHistory(userId, songId);
        }
    }

    public boolean updateSong(Song song) {
        try {
            songDao.save(song);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteSong(int songId) {
        try {
            songDao.deleteById(songId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Song> getTopPlayedSongs(int limit) {
        List<Song> songs = songDao.findTop10ByOrderByPlayCountDesc();
        enrichSongs(songs);
        return songs;
    }

    public int countSongsByArtist(int artistId) {
        return songDao.findByArtistId(artistId).size();
    }
}
