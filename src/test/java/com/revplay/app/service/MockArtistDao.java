package com.revplay.app.service;

import com.revplay.app.repository.IArtistRepo;
import com.revplay.app.entity.ArtistAccount;
import java.util.*;

/**
 * Mock implementation of IArtistRepo for unit testing.
 */
public class MockArtistDao implements IArtistRepo {
    private Map<Integer, ArtistAccount> artists = new HashMap<>();
    private Map<String, ArtistAccount> artistsByEmail = new HashMap<>();
    private int nextId = 1;

    @Override
    public Optional<ArtistAccount> findByEmail(String email) {
        return Optional.ofNullable(artistsByEmail.get(email));
    }

    public ArtistAccount getArtistByEmail(String email) {
        return artistsByEmail.get(email);
    }

    @Override
    public Optional<ArtistAccount> findByStageName(String stageName) {
        return artists.values().stream()
                .filter(a -> stageName.equals(a.getStageName()))
                .findFirst();
    }

    @Override
    public List<ArtistAccount> searchArtists(String keyword) {
        return artists.values().stream()
                .filter(a -> a.getStageName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    @Override
    public int getTotalPlays(int artistId) {
        return 0;
    }

    @Override
    public int getTotalFavorites(int artistId) {
        return 0;
    }

    // JpaRepository methods
    @Override
    public <S extends ArtistAccount> S save(S entity) {
        if (entity.getArtistId() == 0) {
            if (entity.getEmail() != null && artistsByEmail.containsKey(entity.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            entity.setArtistId(nextId++);
        } else {
            // Update case
            ArtistAccount existing = artists.get(entity.getArtistId());
            if (existing != null && entity.getEmail() != null && !entity.getEmail().equals(existing.getEmail())) {
                if (artistsByEmail.containsKey(entity.getEmail())) {
                    throw new RuntimeException("Email already exists");
                }
            }
        }
        artists.put(entity.getArtistId(), entity);
        if (entity.getEmail() != null) {
            artistsByEmail.put(entity.getEmail(), entity);
        }
        return entity;
    }

    public boolean registerArtist(ArtistAccount artist) {
        save(artist);
        return true;
    }

    @Override
    public Optional<ArtistAccount> findById(Integer id) {
        return Optional.ofNullable(artists.get(id));
    }

    @Override
    public List<ArtistAccount> findAll() {
        return new ArrayList<>(artists.values());
    }

    @Override
    public void deleteById(Integer id) {
        ArtistAccount removed = artists.remove(id);
        if (removed != null) {
            artistsByEmail.remove(removed.getEmail());
        }
    }

    // Dummy implementations for other JPA methods to satisfy interface
    @Override
    public List<ArtistAccount> findAll(org.springframework.data.domain.Sort sort) {
        return findAll();
    }

    @Override
    public org.springframework.data.domain.Page<ArtistAccount> findAll(
            org.springframework.data.domain.Pageable pageable) {
        return null;
    }

    @Override
    public <S extends ArtistAccount> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public boolean existsById(Integer id) {
        return artists.containsKey(id);
    }

    @Override
    public long count() {
        return artists.size();
    }

    @Override
    public void delete(ArtistAccount entity) {
        deleteById(entity.getArtistId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
    }

    @Override
    public void deleteAll(Iterable<? extends ArtistAccount> entities) {
    }

    @Override
    public void deleteAll() {
        artists.clear();
        artistsByEmail.clear();
    }

    @Override
    public void flush() {
    }

    @Override
    public <S extends ArtistAccount> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends ArtistAccount> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<ArtistAccount> entities) {
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> ids) {
    }

    @Override
    public void deleteAllInBatch() {
    }

    @Override
    public ArtistAccount getOne(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public ArtistAccount getById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public ArtistAccount getReferenceById(Integer id) {
        return findById(id).orElse(null);
    }

    public ArtistAccount getArtistById(int id) {
        return artists.get(id);
    }

    @Override
    public <S extends ArtistAccount> List<S> findAll(org.springframework.data.domain.Example<S> example) {
        return null;
    }

    @Override
    public <S extends ArtistAccount> List<S> findAll(org.springframework.data.domain.Example<S> example,
            org.springframework.data.domain.Sort sort) {
        return null;
    }

    @Override
    public <S extends ArtistAccount> Optional<S> findOne(org.springframework.data.domain.Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends ArtistAccount> org.springframework.data.domain.Page<S> findAll(
            org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable) {
        return null;
    }

    @Override
    public <S extends ArtistAccount> long count(org.springframework.data.domain.Example<S> example) {
        return 0;
    }

    @Override
    public <S extends ArtistAccount> boolean exists(org.springframework.data.domain.Example<S> example) {
        return false;
    }

    @Override
    public <S extends ArtistAccount, R> R findBy(org.springframework.data.domain.Example<S> example,
            java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<ArtistAccount> findAllById(Iterable<Integer> ids) {
        return null;
    }

    public void clear() {
        artists.clear();
        artistsByEmail.clear();
        nextId = 1;
    }
}
