package com.onlineshop.diploma.controller;

import com.onlineshop.diploma.domain.Game;
import com.onlineshop.diploma.domain.User;
import com.onlineshop.diploma.domain.security.PasswordResetToken;
import com.onlineshop.diploma.domain.security.Role;
import com.onlineshop.diploma.domain.security.UserRole;
import com.onlineshop.diploma.repository.RoleRepository;
import com.onlineshop.diploma.service.GameService;
import com.onlineshop.diploma.service.UserService;
import com.onlineshop.diploma.service.impl.UserSecurityService;
import com.onlineshop.diploma.utility.MailConstructor;
import com.onlineshop.diploma.utility.SecurityUtility;
import freemarker.template.utility.SecurityUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.*;


@Controller
public class HomeController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private MailConstructor mailConstructor;
    @Autowired
    private UserService userService;

    @Autowired
    private UserSecurityService userSecurityService;
    @Autowired
    private GameService gameService;
    @Autowired
    private RoleRepository roleRepository;

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("classActiveLogin", true);
        return "myAccount";
    }

    @RequestMapping("/gameshelf")
    public String gameshelf(Model model) {
        List<Game> gameList = gameService.findAll();
        model.addAttribute("gameList", gameList);

        return "gameshelf";
    }

    @RequestMapping("/gameDetail")
    public String gameDetail(
            @PathParam("id") Long id, Model model, Principal principal
    ){
        if(principal!= null){
            String username = principal.getName();
            User user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }
        Game game = gameService.findById(id);

        model.addAttribute("game", game);
        return "gameDetail";
    }

    @RequestMapping("/forgetPassword")
    public String forgetPassword(HttpServletRequest request,
                                 @ModelAttribute("email") String email,
                                 Model model) {
        model.addAttribute("classActiveForgetPassword", true);

        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("emailNotExist", true);
            return "myAccount";
        }
        String password = SecurityUtility.randomPassword();
        String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
        user.setPassword(encryptedPassword);

        userService.save(user);

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        SimpleMailMessage newEmail = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user, password);
        mailSender.send(newEmail);
        model.addAttribute("forgetPasswordEmailSent", "true");

        return "myAccount";
    }

    @RequestMapping(value = "/newUser", method = RequestMethod.POST)
    public String newUserPost(
            HttpServletRequest request,
            @ModelAttribute("email") String userEmail,
            @ModelAttribute("username") String username,
            Model model
    ) throws Exception {
        model.addAttribute("classActiveNewAccount", true);
        model.addAttribute("email", userEmail);
        model.addAttribute("username", username);

        if (userService.findByUsername(username) != null) {
            model.addAttribute("usernameExists", true);
            return "myAccount";
        }
        if (userService.findByEmail(userEmail) != null) {
            model.addAttribute("emailExists", true);
            return "myAccount";
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(userEmail);

        String password = SecurityUtility.randomPassword();
        String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
        user.setPassword(encryptedPassword);

        Role role = new Role();
        role.setRoleId(1);
        role.setName("ROLE_USER");
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));
        userService.createUser(user, userRoles);

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        SimpleMailMessage email = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user, password);
        mailSender.send(email);
        model.addAttribute("emailSent", "true");
        return "myAccount";
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

        model.addAttribute("user", user);
        model.addAttribute("classActiveEdit", true);
        return "myProfile";
    }
}
