package com.onlineshop.diploma.utility;

import com.onlineshop.diploma.domain.User;
//import org.hibernate.cfg.Environment;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MailConstructor {
    @Autowired
    private Environment env;
    public SimpleMailMessage constructResetTokenEmail(
            String contextPath, Locale locale, String token, User user, String password
    ){
        String url = contextPath + " /newUser?token="+token;
        String message = "\nPlease click on this link to verify your email and edit your personal information";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("OnlineShop - New User!");
        email.setText(url+message);
        email.setFrom(env.getProperty("support email"));
        return email;
    }
}
