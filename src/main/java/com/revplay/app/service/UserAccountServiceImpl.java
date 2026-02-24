package com.revplay.app.service;

import com.revplay.app.entity.UserAccount;
import com.revplay.app.repository.IUserAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserAccountServiceImpl implements IUserAccountService {

    @Autowired
    private IUserAccountRepo userAccountRepo;

    @Override
    public boolean registerUser(UserAccount user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        if (user.getStatus() == null || user.getStatus().isEmpty()) {
            user.setStatus("ACTIVE");
        }
        return userAccountRepo.addUserAccount(user);
    }

    @Override
    public UserAccount getUserById(int userId) {
        return userAccountRepo.getUserAccount(userId);
    }

    @Override
    public UserAccount getUserByEmail(String email) {
        return userAccountRepo.getUserAccountByEmail(email);
    }

    @Override
    public List<UserAccount> getAllUsers() {
        return userAccountRepo.getAllUserAccounts();
    }

    @Override
    public boolean updateUser(UserAccount user) {
        return userAccountRepo.updateUserAccount(user);
    }

    @Override
    public int getPlaylistCount(int userId) {
        return userAccountRepo.getPlaylistCount(userId);
    }

    @Override
    public int getFavoriteCount(int userId) {
        return userAccountRepo.getFavoriteCount(userId);
    }
}
