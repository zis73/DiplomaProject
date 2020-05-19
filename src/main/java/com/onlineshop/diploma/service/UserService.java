package com.onlineshop.diploma.service;

import com.onlineshop.diploma.domain.User;
import com.onlineshop.diploma.domain.security.PasswordResetToken;

public interface UserService {
    PasswordResetToken getPasswordResetToken(final String token);

    void createPasswordResetTokenForUser(final User user, final String token);

    User findByUsername(String username);

    User findByEmail (String email);
}
