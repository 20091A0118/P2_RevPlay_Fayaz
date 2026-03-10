package com.revplay.app.service;

import com.revplay.app.repository.IAlbumRepository;
import com.revplay.app.repository.IArtistRepository;
import com.revplay.app.entity.Album;
import com.revplay.app.entity.ArtistAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AlbumServiceImpl implements IAlbumService {

    private static final Logger logger = LoggerFactory.getLogger(AlbumServiceImpl.class);

    @Autowired
    private IAlbumRepository albumDao;

    @Autowired
    private IArtistRepository artistRepository;

    public void setAlbumDao(IAlbumRepository albumDao) {
        this.albumDao = albumDao;
    }

    private void enrichAlbums(List<Album> albums) {
        if (albums == null || albums.isEmpty())
            return;
        Map<Integer, String> artistNames = artistRepository.findAll().stream()
                .collect(Collectors.toMap(ArtistAccount::getArtistId,
                        artist -> artist.getStageName() != null ? artist.getStageName() : "Unknown Artist",
                        (a, b) -> a));
        for (Album a : albums) {
            a.setArtistName(artistNames.getOrDefault(a.getArtistId(), "Unknown Artist"));
        }
    }

    public boolean createAlbum(Album album) {
        logger.info("createAlbum method called in Service");
        try {
            if (album.getCreatedAt() == null)
                album.setCreatedAt(LocalDateTime.now());
            albumDao.save(album);
            return true;
        } catch (Exception e) {
            System.err.println("Album creation failed: " + e.getMessage());
            return false;
        }
    }

    public Album getAlbumById(int albumId) {
        logger.info("getAlbumById method called in Service for albumId: {}", albumId);
        Album album = albumDao.findById(albumId).orElse(null);
        if (album != null) {
            enrichAlbums(List.of(album));
        }
        return album;
    }

    public List<Album> getAlbumsByArtist(int artistId) {
        logger.info("getAlbumsByArtist method called in Service for artistId: {}", artistId);
        List<Album> albums = albumDao.findByArtistId(artistId);
        enrichAlbums(albums);
        return albums;
    }

    public List<Album> getAllAlbums() {
        logger.info("getAllAlbums method called in Service");
        List<Album> albums = albumDao.findAll();
        enrichAlbums(albums);
        return albums;
    }

    public boolean updateAlbum(Album album) {
        logger.info("updateAlbum method called in Service for albumId: {}", album.getAlbumId());
        try {
            return albumDao.findById(album.getAlbumId()).map(existing -> {
                if (album.getTitle() != null)
                    existing.setTitle(album.getTitle());
                if (album.getDescription() != null)
                    existing.setDescription(album.getDescription());
                if (album.getReleaseDate() != null)
                    existing.setReleaseDate(album.getReleaseDate());
                if (album.getCoverImageUrl() != null)
                    existing.setCoverImageUrl(album.getCoverImageUrl());
                albumDao.save(existing);
                return true;
            }).orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteAlbum(int albumId) {
        logger.info("deleteAlbum method called in Service for albumId: {}", albumId);
        try {
            albumDao.deleteById(albumId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
