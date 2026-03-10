package com.revplay.app.service;

import com.revplay.app.repository.ISongRepository;
import com.revplay.app.repository.IArtistRepository;
import com.revplay.app.repository.IAlbumRepository;
import com.revplay.app.repository.IGenreRepository;
import com.revplay.app.entity.Song;
import com.revplay.app.entity.Album;
import com.revplay.app.entity.ArtistAccount;
import com.revplay.app.entity.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revplay.app.service.IHistoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SongServiceImpl implements ISongService {

    private static final Logger logger = LoggerFactory.getLogger(SongServiceImpl.class);

    @Autowired
    private ISongRepository songDao;

    @Autowired
    private IHistoryService historyService;

    @Autowired
    private IArtistRepository artistRepository;

    @Autowired
    private IGenreRepository genreRepository;

    @Autowired
    private IAlbumRepository albumRepository;

    public void setSongDao(ISongRepository songDao) {
        this.songDao = songDao;
    }

    // Populate transient artistName/genreName/albumTitle on a list of songs
    public void enrichSongs(List<Song> songs) {
        logger.info("enrichSongs method called in Service");
        if (songs == null || songs.isEmpty())
            return;
        Map<Integer, String> artistNames = artistRepository.findAll().stream()
                .collect(Collectors.toMap(ArtistAccount::getArtistId,
                        artist -> artist.getStageName() != null ? artist.getStageName() : "Unknown Artist",
                        (a, b) -> a));
        Map<Integer, String> genreNames = genreRepository.findAll().stream()
                .collect(Collectors.toMap(Genre::getGenreId, Genre::getGenreName, (a, b) -> a));
        Map<Integer, String> albumTitles = albumRepository.findAll().stream()
                .collect(Collectors.toMap(Album::getAlbumId, Album::getTitle, (a, b) -> a));
        for (Song s : songs) {
            s.setArtistName(artistNames.getOrDefault(s.getArtistId(), "Unknown Artist"));
            s.setGenreName(genreNames.getOrDefault(s.getGenreId(), "-"));
            if (s.getAlbumId() != null) {
                s.setAlbumTitle(albumTitles.getOrDefault(s.getAlbumId(), null));
            }
        }
    }

    public boolean uploadSong(Song song) {
        logger.info("uploadSong method called in Service");
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
        logger.info("getAllSongs method called in Service");
        List<Song> songs = songDao.findAll();
        enrichSongs(songs);
        return songs;
    }

    public Song getSongById(int songId) {
        logger.info("getSongById method called in Service for songId: {}", songId);
        Song song = songDao.findById(songId).orElse(null);
        if (song != null) {
            enrichSongs(java.util.List.of(song));
        }
        return song;
    }

    public List<Song> getSongsByArtist(int artistId) {
        logger.info("getSongsByArtist method called in Service for artistId: {}", artistId);
        List<Song> songs = songDao.findByArtistId(artistId);
        enrichSongs(songs);
        return songs;
    }

    public List<Song> getSongsByAlbum(int albumId) {
        logger.info("getSongsByAlbum method called in Service for albumId: {}", albumId);
        List<Song> songs = songDao.findByAlbumId(albumId);
        enrichSongs(songs);
        return songs;
    }

    public List<Song> getSongsByGenre(int genreId) {
        logger.info("getSongsByGenre method called in Service for genreId: {}", genreId);
        List<Song> songs = songDao.findByGenreId(genreId);
        enrichSongs(songs);
        return songs;
    }

    public List<Song> searchSongs(String keyword) {
        logger.info("searchSongs method called in Service with keyword: {}", keyword);
        List<Song> songs = songDao.searchSongs(keyword);
        enrichSongs(songs);
        return songs;
    }

    public void playSong(int songId, Integer userId) {
        logger.info("playSong method called in Service for songId: {}", songId);
        songDao.findById(songId).ifPresent(song -> {
            song.setPlayCount(song.getPlayCount() + 1);
            songDao.save(song);
        });
        if (userId != null) {
            historyService.addHistory(userId, songId);
        }
    }

    public boolean updateSong(Song song) {
        logger.info("updateSong method called in Service for songId: {}", song.getSongId());
        try {
            return songDao.findById(song.getSongId()).map(existing -> {
                if (song.getTitle() != null)
                    existing.setTitle(song.getTitle());
                if (song.getAlbumId() != null)
                    existing.setAlbumId(song.getAlbumId());
                if (song.getGenreId() != 0)
                    existing.setGenreId(song.getGenreId());
                if (song.getDurationSeconds() != 0)
                    existing.setDurationSeconds(song.getDurationSeconds());
                if (song.getReleaseDate() != null)
                    existing.setReleaseDate(song.getReleaseDate());
                if (song.getFileUrl() != null)
                    existing.setFileUrl(song.getFileUrl());
                if (song.getIsActive() != null)
                    existing.setIsActive(song.getIsActive());
                if (song.getCoverImageUrl() != null)
                    existing.setCoverImageUrl(song.getCoverImageUrl());
                songDao.save(existing);
                return true;
            }).orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteSong(int songId) {
        logger.info("deleteSong method called in Service for songId: {}", songId);
        try {
            songDao.deleteById(songId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Song> getTopPlayedSongs(int limit) {
        logger.info("getTopPlayedSongs method called in Service with limit: {}", limit);
        List<Song> songs = songDao.findTop10ByOrderByPlayCountDesc();
        enrichSongs(songs);
        return songs;
    }

    public int countSongsByArtist(int artistId) {
        logger.info("countSongsByArtist method called in Service for artistId: {}", artistId);
        return songDao.findByArtistId(artistId).size();
    }
}
