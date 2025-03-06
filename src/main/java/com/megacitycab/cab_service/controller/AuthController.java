package com.megacitycab.cab_service.controller;

import com.megacitycab.cab_service.model.Customer;
import com.megacitycab.cab_service.model.User;
import com.megacitycab.cab_service.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String role,
                           Customer customer,
                           Model model) {
        // Check if username already exists
        if (adminService.usernameExists(username)) {
            model.addAttribute("error", "Username already exists. Please choose a different username.");
            model.addAttribute("user", new User());
            model.addAttribute("customer", customer);
            model.addAttribute("selectedRole", role);
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        adminService.createUser(user, role.equalsIgnoreCase("customer") ? customer : null, role);
        return "redirect:/login?registered=true";
    }
}