package com.revplay.app.repository;

import com.revplay.app.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IGenreRepo extends JpaRepository<Genre, Integer> {

    Optional<Genre> findByGenreName(String genreName);
}
