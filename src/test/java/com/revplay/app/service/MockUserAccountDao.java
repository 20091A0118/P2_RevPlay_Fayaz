package com.revplay.app.service;

import com.revplay.app.repository.IUserAccountRepo;
import com.revplay.app.entity.UserAccount;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

/**
 * Mock implementation of IUserAccountRepo for unit testing.
 */
public class MockUserAccountDao implements IUserAccountRepo {
    private Map<Integer, UserAccount> users = new HashMap<>();
    private Map<String, UserAccount> usersByEmail = new HashMap<>();
    private int nextId = 1;

    // Custom methods from IUserAccountRepo
    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    // Legacy helpers for tests
    public boolean addUserAccount(UserAccount user) {
        save(user);
        return true;
    }

    public boolean updateUserAccount(UserAccount user) {
        save(user);
        return true;
    }

    public UserAccount getUserAccount(int userId) {
        return findById(userId).orElse(null);
    }

    public List<UserAccount> getAllUserAccounts() {
        return findAll();
    }

    public UserAccount getUserAccountByEmail(String email) {
        return usersByEmail.get(email);
    }

    public int getPlaylistCount(int userId) {
        return 0;
    }

    public int getFavoriteCount(int userId) {
        return 0;
    }

    // JpaRepository methods
    @Override
    public <S extends UserAccount> S save(S entity) {
        if (entity.getUserId() == 0) {
            if (entity.getEmail() != null && usersByEmail.containsKey(entity.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            entity.setUserId(nextId++);
        } else {
            // Update case: check if email is changed to an existing one
            UserAccount existing = users.get(entity.getUserId());
            if (existing != null && entity.getEmail() != null && !entity.getEmail().equals(existing.getEmail())) {
                if (usersByEmail.containsKey(entity.getEmail())) {
                    throw new RuntimeException("Email already exists");
                }
            }
        }
        users.put(entity.getUserId(), entity);
        if (entity.getEmail() != null) {
            usersByEmail.put(entity.getEmail(), entity);
        }
        return entity;
    }

    @Override
    public Optional<UserAccount> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<UserAccount> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Integer id) {
        UserAccount removed = users.remove(id);
        if (removed != null) {
            usersByEmail.remove(removed.getEmail());
        }
    }

    @Override
    public List<UserAccount> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<UserAccount> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends UserAccount> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public boolean existsById(Integer id) {
        return users.containsKey(id);
    }

    @Override
    public long count() {
        return users.size();
    }

    @Override
    public void delete(UserAccount entity) {
        deleteById(entity.getUserId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
    }

    @Override
    public void deleteAll(Iterable<? extends UserAccount> entities) {
    }

    @Override
    public void deleteAll() {
        users.clear();
        usersByEmail.clear();
    }

    @Override
    public void flush() {
    }

    @Override
    public <S extends UserAccount> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends UserAccount> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<UserAccount> entities) {
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> ids) {
    }

    @Override
    public void deleteAllInBatch() {
    }

    @Override
    public UserAccount getOne(Integer id) {
        return getById(id);
    }

    @Override
    public UserAccount getById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public UserAccount getReferenceById(Integer id) {
        return findById(id).orElse(null);
    }

    @Override
    public <S extends UserAccount> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends UserAccount> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends UserAccount> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends UserAccount> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends UserAccount> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends UserAccount> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends UserAccount, R> R findBy(Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<UserAccount> findAllById(Iterable<Integer> ids) {
        return null;
    }

    public void clear() {
        users.clear();
        usersByEmail.clear();
        nextId = 1;
    }
}
