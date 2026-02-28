package com.revplay.app.service;

import com.revplay.app.repository.IFavoriteRepo;
import com.revplay.app.entity.FavoriteSong;
import com.revplay.app.entity.Song;
import com.revplay.app.repository.ISongRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteServiceImpl implements IFavoriteService {

    @Autowired
    private IFavoriteRepo favoriteDao;

    @Autowired
    private ISongService songService;

    @Autowired
    private ISongRepo songRepo;

    public boolean addFavorite(int userId, int songId) {
        try {
            if (favoriteDao.findByUserIdAndSongId(userId, songId).isEmpty()) {
                FavoriteSong fav = new FavoriteSong();
                fav.setUserId(userId);
                fav.setSongId(songId);
                fav.setFavoritedAt(LocalDateTime.now());
                favoriteDao.save(fav);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error adding favorite: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFavorite(int userId, int songId) {
        return favoriteDao.findByUserIdAndSongId(userId, songId).map(fav -> {
            favoriteDao.delete(fav);
            return true;
        }).orElse(false);
    }

    public List<Song> getFavoriteSongs(int userId) {
        List<com.revplay.app.entity.FavoriteSong> favorites = favoriteDao.findByUserIdOrderByFavoritedAtDesc(userId);
        List<Integer> songIds = favorites.stream()
                .map(com.revplay.app.entity.FavoriteSong::getSongId)
                .collect(java.util.stream.Collectors.toList());

        if (songIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<Song> songs = songRepo.findAllById(songIds);
        songService.enrichSongs(songs);
        return songs;
    }

    public boolean isFavorite(int userId, int songId) {
        return favoriteDao.findByUserIdAndSongId(userId, songId).isPresent();
    }
}
