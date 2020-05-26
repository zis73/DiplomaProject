package com.onlineshop.diploma;

import com.onlineshop.diploma.domain.User;
import com.onlineshop.diploma.domain.security.Role;
import com.onlineshop.diploma.domain.security.UserRole;
import com.onlineshop.diploma.service.UserService;
import com.onlineshop.diploma.utility.SecurityUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class DiplomaApplication implements CommandLineRunner {
    @Autowired
    private UserService userService;
    public static void main(String[] args) {
        SpringApplication.run(DiplomaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User user1 = new User();
        user1.setFirstName("Ivan");
        user1.setLastName("Ivanov");
        user1.setUsername("i");
        user1.setPassword(SecurityUtility.passwordEncoder().encode("p"));
        user1.setEmail("IIvanov@gmail.com");
        Set<UserRole> userRoles = new HashSet<>();
        Role role1 = new Role();
        role1.setRoleId(1);
        role1.setName("ROLE_user");
        userRoles.add(new UserRole(user1, role1));

        userService.createUser(user1, userRoles);
    }
}
