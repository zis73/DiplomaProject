package com.onlineshop.diploma.service.impl;

import com.onlineshop.diploma.domain.User;
import com.onlineshop.diploma.domain.security.PasswordResetToken;
import com.onlineshop.diploma.repository.PasswordResetTokenRepository;
import com.onlineshop.diploma.repository.UserRepository;
import com.onlineshop.diploma.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public void createPasswordResetTokenForUser(final User user, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token,user);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public User findByUsername(String username){
        return  userRepository.findByUsername(username);
    }

    public  User findByEmail (String email){
        return userRepository.findByEmail(email);
    }
}
