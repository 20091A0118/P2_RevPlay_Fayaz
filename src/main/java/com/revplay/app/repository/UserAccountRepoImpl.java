package com.revplay.app.repository;

import com.revplay.app.entity.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserAccountRepoImpl implements IUserAccountRepo {

    private static final Logger logger = LogManager.getLogger(UserAccountRepoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<UserAccount> rowMapper = (rs, rowNum) -> {
        UserAccount user = new UserAccount();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setPhone(rs.getString("phone"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setSecurityQuestion(rs.getString("security_question"));
        user.setSecurityAnswerHash(rs.getString("security_answer_hash"));
        user.setPasswordHint(rs.getString("password_hint"));
        return user;
    };

    public boolean addUserAccount(UserAccount user) {
        String sql = "INSERT INTO USER_ACCOUNT (full_name, email, password_hash, phone, status, created_at, security_question, security_answer_hash, password_hint) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            int rows = jdbcTemplate.update(sql,
                    user.getFullName(), user.getEmail(), user.getPasswordHash(),
                    user.getPhone(), user.getStatus(),
                    java.sql.Timestamp.valueOf(user.getCreatedAt()),
                    user.getSecurityQuestion(), user.getSecurityAnswerHash(),
                    user.getPasswordHint());
            return rows > 0;
        } catch (DataAccessException e) {
            logger.error("Error in addUserAccount: {}", e.getMessage());
            return false;
        }
    }

    public boolean updateUserAccount(UserAccount user) {
        String sql = "UPDATE USER_ACCOUNT SET full_name = ?, password_hash = ?, phone = ?, status = ?, security_question = ?, security_answer_hash = ?, password_hint = ? WHERE user_id = ?";
        try {
            int rows = jdbcTemplate.update(sql,
                    user.getFullName(), user.getPasswordHash(), user.getPhone(),
                    user.getStatus(), user.getSecurityQuestion(),
                    user.getSecurityAnswerHash(), user.getPasswordHint(),
                    user.getUserId());
            return rows > 0;
        } catch (DataAccessException e) {
            logger.error("Error in updateUserAccount: {}", e.getMessage());
            return false;
        }
    }

    public UserAccount getUserAccount(int userId) {
        String sql = "SELECT * FROM USER_ACCOUNT WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserAccount getUserAccountByEmail(String email) {
        String sql = "SELECT * FROM USER_ACCOUNT WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<UserAccount> getAllUserAccounts() {
        String sql = "SELECT * FROM USER_ACCOUNT";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public int getPlaylistCount(int userId) {
        String sql = "SELECT COUNT(*) FROM PLAYLIST WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    public int getFavoriteCount(int userId) {
        String sql = "SELECT COUNT(*) FROM FAVORITE_SONG WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }
}
