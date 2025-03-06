package com.megacitycab.cab_service.controller;

import com.megacitycab.cab_service.model.Role;
import com.megacitycab.cab_service.model.User;
import com.megacitycab.cab_service.repository.RoleRepository;
import com.megacitycab.cab_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.Set;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        String roleName = switch (role.toLowerCase()) {
            case "customer" -> "ROLE_CUSTOMER";
            case "moderator" -> "ROLE_MODERATOR";
            case "admin" -> "ROLE_ADMIN";
            default -> throw new IllegalArgumentException("Invalid role");
        };

        Role userRole = roleRepository.findByName(roleName);
        if (userRole == null) {
            userRole = new Role(roleName);
            roleRepository.save(userRole);
        }
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard() {
        return "customer-dashboard";
    }

    @GetMapping("/moderator/dashboard")
    public String moderatorDashboard() {
        return "moderator-dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }
}