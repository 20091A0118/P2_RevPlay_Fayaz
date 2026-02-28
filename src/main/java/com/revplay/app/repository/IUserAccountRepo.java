package com.revplay.app.repository;

import com.revplay.app.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserAccountRepo extends JpaRepository<UserAccount, Integer> {

    Optional<UserAccount> findByEmail(String email);
}
