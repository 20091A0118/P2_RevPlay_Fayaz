package com.revplay.app.repository;

import com.revplay.app.entity.ListeningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IListeningHistoryRepository extends JpaRepository<ListeningHistory, Integer> {

    List<ListeningHistory> findByUserIdOrderByPlayedAtDesc(int userId);

    long countByUserId(int userId);

    void deleteByUserId(int userId);

    @Query("SELECT h.userId, COUNT(h) as cnt FROM ListeningHistory h JOIN Song s ON h.songId = s.songId WHERE s.artistId = :artistId GROUP BY h.userId ORDER BY cnt DESC")
    List<Object[]> findTopListenersByArtistId(@Param("artistId") int artistId);

    @Query("SELECT h FROM ListeningHistory h JOIN Song s ON h.songId = s.songId WHERE s.artistId = :artistId ORDER BY h.playedAt DESC")
    List<ListeningHistory> findHistoryByArtistId(@Param("artistId") int artistId);
}
