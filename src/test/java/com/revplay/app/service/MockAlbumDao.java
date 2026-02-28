package com.revplay.app.service;

import com.revplay.app.repository.IAlbumRepo;
import com.revplay.app.entity.Album;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mock implementation of IAlbumRepo for unit testing.
 */
public class MockAlbumDao implements IAlbumRepo {
    private Map<Integer, Album> albums = new HashMap<>();
    private int nextId = 1;

    // Custom methods from IAlbumRepo
    @Override
    public List<Album> findByArtistId(int artistId) {
        return albums.values().stream()
                .filter(a -> a.getArtistId() == artistId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Album> searchAlbums(String keyword) {
        return albums.values().stream()
                .filter(a -> a.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Legacy helpers for tests
    public boolean createAlbum(Album album) {
        save(album);
        return true;
    }

    public Album getAlbumById(int albumId) {
        return findById(albumId).orElse(null);
    }

    public List<Album> getAlbumsByArtistId(int artistId) {
        return findByArtistId(artistId);
    }

    public boolean updateAlbum(Album album) {
        save(album);
        return true;
    }

    public boolean deleteAlbum(int albumId) {
        deleteById(albumId);
        return true;
    }

    public List<Album> getAllAlbums() {
        return findAll();
    }

    // JpaRepository methods
    @Override
    public <S extends Album> S save(S entity) {
        if (entity.getAlbumId() == 0) {
            entity.setAlbumId(nextId++);
        }
        albums.put(entity.getAlbumId(), entity);
        return entity;
    }

    @Override
    public Optional<Album> findById(Integer id) {
        return Optional.ofNullable(albums.get(id));
    }

    @Override
    public List<Album> findAll() {
        return new ArrayList<>(albums.values());
    }

    @Override
    public void deleteById(Integer id) {
        albums.remove(id);
    }

    @Override
    public List<Album> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<Album> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Album> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public boolean existsById(Integer id) {
        return albums.containsKey(id);
    }

    @Override
    public long count() {
        return albums.size();
    }

    @Override
    public void delete(Album entity) {
        deleteById(entity.getAlbumId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
    }

    @Override
    public void deleteAll(Iterable<? extends Album> entities) {
    }

    @Override
    public void deleteAll() {
        albums.clear();
    }

    @Override
    public void flush() {
    }

    @Override
    public <S extends Album> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends Album> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Album> entities) {
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> ids) {
    }

    @Override
    public void deleteAllInBatch() {
    }

    @Override
    public Album getOne(Integer id) {
        return getById(id);
    }

    @Override
    public Album getById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public Album getReferenceById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public <S extends Album> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Album> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Album> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Album> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Album> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Album> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Album, R> R findBy(Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Album> findAllById(Iterable<Integer> ids) {
        return null;
    }

    public void clear() {
        albums.clear();
        nextId = 1;
    }
}
