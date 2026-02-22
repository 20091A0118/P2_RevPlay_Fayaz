package com.revplay.app.repository;

import com.revplay.app.entity.Album;
import java.util.List;

public interface IAlbumRepo {
    boolean createAlbum(Album album);

    Album getAlbumById(int albumId);

    List<Album> getAlbumsByArtistId(int artistId);

    List<Album> getAllAlbums();

    boolean updateAlbum(Album album);

    boolean deleteAlbum(int albumId);
}
