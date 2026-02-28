package com.revplay.app.repository;

import com.revplay.app.entity.ListeningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IListeningHistoryRepo extends JpaRepository<ListeningHistory, Integer> {

    List<ListeningHistory> findByUserIdOrderByPlayedAtDesc(int userId);

    long countByUserId(int userId);

    void deleteByUserId(int userId);
}
