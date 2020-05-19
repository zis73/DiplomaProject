package com.onlineshop.diploma.controller;

import com.onlineshop.diploma.domain.User;
import com.onlineshop.diploma.domain.security.PasswordResetToken;
import com.onlineshop.diploma.domain.security.Role;
import com.onlineshop.diploma.domain.security.UserRole;
import com.onlineshop.diploma.service.UserService;
import com.onlineshop.diploma.service.impl.UserSecurityService;
import com.onlineshop.diploma.utility.SecurityUtility;
import freemarker.template.utility.SecurityUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Controller
public class HomeController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserSecurityService userSecurityService;

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("classActiveLogin", true);
        return "myAccount";
    }

    @RequestMapping("/forgetPassword")
    public String forgetPassword(Model model) {
        model.addAttribute("classActiveForgetPassword", true);
        return "myAccount";
    }
    @RequestMapping(value = "/newUser", method= RequestMethod.POST)
    public String newUserPost(
            HttpServletRequest request,
            @ModelAttribute("email") String userEmail,
            @ModelAttribute("username") String username,
            Model model
    ) throws Exception{
        model.addAttribute("classActiveNewAccount", true);
        model.addAttribute("email", userEmail);
        model.addAttribute("username", username);
        if (userService.findByUsername(username) != null){
            model.addAllAttributes("usernameExists", true);
            return "myAccount";
        }
        if (userService.findByEmail(userEmail) != null){
            model.addAllAttributes("email", true);
            return "myAccount";
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(userEmail);

        String password = SecurityUtility.randomPassword();
        String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
        user.setPassword(encryptedPassword);

        Role role = new Role();
        role.setName("ROLE_USER");
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));
        userService.createUser(user, userRoles);
    }

    @RequestMapping("/newUser")
    public String newUser(Locale locale,
                          @RequestParam("token") String token,
                          Model model) {
        PasswordResetToken passToken = userService.getPasswordResetToken(token);
        if (passToken == null) {
            String message = "Invalid Token.";
            model.addAttribute("message", message);
            return "redirect:/badRequest";
        }
        User user = passToken.getUser();
        String username = user.getUsername();

        UserDetails userDetails = userSecurityService.loadUserByUsername(username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("classActiveEdit", true);
        return "myProfile";
    }
}
