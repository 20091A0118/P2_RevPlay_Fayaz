package com.revplay.app.service;

import com.revplay.app.entity.UserAccount;
import java.util.Map;

public interface IUserAccountService {
    Map<String, Object> registerUser(UserAccount user);

    Map<String, Object> loginUser(String email, String password);

    UserAccount getUserById(int userId);

    UserAccount getUserByEmail(String email);

    boolean updateProfile(UserAccount user);

    Map<String, Object> getUserStats(int userId);

    Map<String, Object> forgotPassword(String email, String securityAnswer, String newPassword);

    Map<String, Object> getSecurityQuestion(String email);

    boolean updatePassword(int userId, String oldPassword, String newPassword);
}
