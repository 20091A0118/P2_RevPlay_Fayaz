package com.revplay.app.service;

import com.revplay.app.repository.IFavoriteRepository;
import com.revplay.app.entity.FavoriteSong;
import com.revplay.app.entity.Song;
import com.revplay.app.repository.ISongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FavoriteServiceImpl implements IFavoriteService {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteServiceImpl.class);

    @Autowired
    private IFavoriteRepository favoriteDao;

    @Autowired
    private ISongService songService;

    @Autowired
    private ISongRepository songRepository;

    public boolean addFavorite(int userId, int songId) {
        logger.info("addFavorite method called in Service for userId: {}, songId: {}", userId, songId);
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
        logger.info("removeFavorite method called in Service for userId: {}, songId: {}", userId, songId);
        return favoriteDao.findByUserIdAndSongId(userId, songId).map(fav -> {
            favoriteDao.delete(fav);
            return true;
        }).orElse(false);
    }

    public List<Song> getFavoriteSongs(int userId) {
        logger.info("getFavoriteSongs method called in Service for userId: {}", userId);
        List<com.revplay.app.entity.FavoriteSong> favorites = favoriteDao.findByUserIdOrderByFavoritedAtDesc(userId);
        List<Integer> songIds = favorites.stream()
                .map(com.revplay.app.entity.FavoriteSong::getSongId)
                .collect(java.util.stream.Collectors.toList());

        if (songIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<Song> songs = songRepository.findAllById(songIds);
        songService.enrichSongs(songs);
        return songs;
    }

    public boolean isFavorite(int userId, int songId) {
        logger.info("isFavorite method called in Service for userId: {}, songId: {}", userId, songId);
        return favoriteDao.findByUserIdAndSongId(userId, songId).isPresent();
    }
}
