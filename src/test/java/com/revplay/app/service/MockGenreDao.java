package com.revplay.app.service;

import com.revplay.app.repository.IGenreRepo;
import com.revplay.app.entity.Genre;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

/**
 * Mock implementation of IGenreRepo for unit testing.
 */
public class MockGenreDao implements IGenreRepo {
    private Map<Integer, Genre> genres = new HashMap<>();
    private int nextId = 1;

    // Custom methods from IGenreRepo
    @Override
    public Optional<Genre> findByGenreName(String genreName) {
        return genres.values().stream()
                .filter(g -> g.getGenreName().equalsIgnoreCase(genreName))
                .findFirst();
    }

    // Legacy helpers for tests
    public boolean addGenre(Genre genre) {
        save(genre);
        return true;
    }

    public boolean addGenre(String genreName) {
        if (findByGenreName(genreName).isPresent()) {
            return false;
        }
        Genre genre = new Genre();
        genre.setGenreName(genreName);
        save(genre);
        return true;
    }

    public List<Genre> getAllGenres() {
        return findAll();
    }

    public Genre getGenreById(int id) {
        return findById(id).orElse(null);
    }

    public Genre getGenreByName(String name) {
        return findByGenreName(name).orElse(null);
    }

    // JpaRepository methods
    @Override
    public <S extends Genre> S save(S entity) {
        if (entity.getGenreId() == 0) {
            entity.setGenreId(nextId++);
        }
        genres.put(entity.getGenreId(), entity);
        return entity;
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        return Optional.ofNullable(genres.get(id));
    }

    @Override
    public List<Genre> findAll() {
        return new ArrayList<>(genres.values());
    }

    @Override
    public void deleteById(Integer id) {
        genres.remove(id);
    }

    @Override
    public List<Genre> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<Genre> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Genre> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public boolean existsById(Integer id) {
        return genres.containsKey(id);
    }

    @Override
    public long count() {
        return genres.size();
    }

    @Override
    public void delete(Genre entity) {
        deleteById(entity.getGenreId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
    }

    @Override
    public void deleteAll(Iterable<? extends Genre> entities) {
    }

    @Override
    public void deleteAll() {
        genres.clear();
    }

    @Override
    public void flush() {
    }

    @Override
    public <S extends Genre> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends Genre> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Genre> entities) {
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> ids) {
    }

    @Override
    public void deleteAllInBatch() {
    }

    @Override
    public Genre getOne(Integer id) {
        return getById(id);
    }

    @Override
    public Genre getById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public Genre getReferenceById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public <S extends Genre> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Genre> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Genre> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Genre> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Genre> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Genre> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Genre, R> R findBy(Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Genre> findAllById(Iterable<Integer> ids) {
        return null;
    }

    public Genre getOrCreateGenre(String genreName) {
        Genre existing = getGenreByName(genreName);
        if (existing != null)
            return existing;
        addGenre(genreName);
        return getGenreByName(genreName);
    }

    public void clear() {
        genres.clear();
        nextId = 1;
    }
}
