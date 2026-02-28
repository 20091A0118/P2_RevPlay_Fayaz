package com.revplay.app.repository;

import com.revplay.app.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPlaylistRepo extends JpaRepository<Playlist, Integer> {

    List<Playlist> findByUserIdOrderByUpdatedAtDesc(int userId);

    List<Playlist> findByPrivacyStatusOrderByUpdatedAtDesc(String privacyStatus);

    long countByUserId(int userId);
}
