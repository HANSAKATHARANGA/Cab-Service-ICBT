package com.megacitycab.cab_service.controller;

import com.megacitycab.cab_service.model.*;
import com.megacitycab.cab_service.repository.CustomerRepository;
import com.megacitycab.cab_service.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    @Autowired private AdminService adminService;
    @Autowired private CustomerRepository customerRepository;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin-dashboard";
    }

    // Cars
    @GetMapping("/cars")
    public String cars(Model model) {
        model.addAttribute("cars", adminService.getAllCars());
        model.addAttribute("car", new Car());
        return "admin-cars";
    }

    @PostMapping("/cars/create")
    public String createCar(@Valid @ModelAttribute Car car, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("cars", adminService.getAllCars());
            return "admin-cars";
        }
        adminService.createCar(car);
        return "redirect:/admin/cars";
    }

    @GetMapping("/cars/edit/{id}")
    public String editCar(@PathVariable Long id, Model model) {
        model.addAttribute("car", adminService.getCar(id));
        model.addAttribute("cars", adminService.getAllCars());
        return "admin-cars";
    }

    @PostMapping("/cars/update")
    public String updateCar(@Valid @ModelAttribute Car car, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("cars", adminService.getAllCars());
            return "admin-cars";
        }
        adminService.updateCar(car);
        return "redirect:/admin/cars";
    }

    @GetMapping("/cars/delete/{id}")
    public String deleteCar(@PathVariable Long id) {
        adminService.deleteCar(id);
        return "redirect:/admin/cars";
    }

    // Drivers
    @GetMapping("/drivers")
    public String drivers(Model model) {
        model.addAttribute("drivers", adminService.getAllDrivers());
        model.addAttribute("driver", new Driver());
        model.addAttribute("cars", adminService.getAllCars());
        return "admin-drivers";
    }

    @PostMapping("/drivers/create")
    public String createDriver(@Valid @ModelAttribute Driver driver, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("drivers", adminService.getAllDrivers());
            model.addAttribute("cars", adminService.getAllCars());
            return "admin-drivers";
        }
        adminService.createDriver(driver);
        return "redirect:/admin/drivers";
    }

    @GetMapping("/drivers/edit/{id}")
    public String editDriver(@PathVariable Long id, Model model) {
        model.addAttribute("driver", adminService.getDriver(id));
        model.addAttribute("drivers", adminService.getAllDrivers());
        model.addAttribute("cars", adminService.getAllCars());
        return "admin-drivers";
    }

    @PostMapping("/drivers/update")
    public String updateDriver(@Valid @ModelAttribute Driver driver, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("drivers", adminService.getAllDrivers());
            model.addAttribute("cars", adminService.getAllCars());
            return "admin-drivers";
        }
        adminService.updateDriver(driver);
        return "redirect:/admin/drivers";
    }

    @GetMapping("/drivers/delete/{id}")
    public String deleteDriver(@PathVariable Long id) {
        adminService.deleteDriver(id);
        return "redirect:/admin/drivers";
    }

    // Users
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        model.addAttribute("user", new User());
        model.addAttribute("customer", new Customer());
        return "admin-users";
    }

    @PostMapping("/users/create")
    public String createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            @Valid @ModelAttribute Customer customer,
            BindingResult result,
            Model model) {
        if (result.hasErrors() && role.equalsIgnoreCase("customer")) {
            model.addAttribute("users", adminService.getAllUsers());
            model.addAttribute("user", new User(username, password, null));
            model.addAttribute("customer", customer);
            model.addAttribute("selectedRole", role);
            return "admin-users";
        }
        // Check if username exists
        if (adminService.usernameExists(username)) {
            model.addAttribute("error", "Username already exists. Please choose a different username.");
            model.addAttribute("users", adminService.getAllUsers());
            model.addAttribute("user", new User(username, password, null));
            model.addAttribute("customer", customer);
            model.addAttribute("selectedRole", role);
            return "admin-users";
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        adminService.createUser(user, role.equalsIgnoreCase("customer") ? customer : null, role);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = adminService.getUser(id);
        Customer customer = user != null ? customerRepository.findByUser(user) : new Customer();
        model.addAttribute("user", user);
        model.addAttribute("customer", customer != null ? customer : new Customer());
        model.addAttribute("users", adminService.getAllUsers());
        model.addAttribute("selectedRole", user.getRoles().stream().findFirst()
                .map(role -> role.getName().replace("ROLE_", "").toLowerCase()).orElse(""));
        return "admin-users";
    }

    @PostMapping("/users/update")
    public String updateUser(
            @RequestParam Long id,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            @Valid @ModelAttribute Customer customer,
            BindingResult result,
            Model model) {
        if (result.hasErrors() && role.equalsIgnoreCase("customer")) {
            User user = new User(username, password, null);
            user.setId(id);
            model.addAttribute("users", adminService.getAllUsers());
            model.addAttribute("user", user);
            model.addAttribute("customer", customer);
            model.addAttribute("selectedRole", role);
            return "admin-users";
        }
        User user = adminService.getUser(id);
        if (user == null) {
            return "redirect:/admin/users";
        }
        // Check if username changed and already exists
        if (!user.getUsername().equals(username) && adminService.usernameExists(username)) {
            model.addAttribute("error", "Username already exists. Please choose a different username.");
            model.addAttribute("users", adminService.getAllUsers());
            model.addAttribute("user", user);
            model.addAttribute("customer", customer);
            model.addAttribute("selectedRole", role);
            return "admin-users";
        }
        user.setUsername(username);
        user.setPassword(password); // Will be encoded in service
        adminService.updateUser(user); // Update user roles and customer separately if needed
        if (role.equalsIgnoreCase("customer")) {
            Customer existingCustomer = customerRepository.findByUser(user);
            if (existingCustomer != null) {
                existingCustomer.setName(customer.getName());
                existingCustomer.setAddress(customer.getAddress());
                existingCustomer.setNic(customer.getNic());
                customerRepository.save(existingCustomer);
            } else {
                customer.setUser(user);
                customerRepository.save(customer);
            }
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        User user = adminService.getUser(id);
        if (user != null) {
            Customer customer = customerRepository.findByUser(user);
            if (customer != null) {
                customerRepository.delete(customer);
            }
            adminService.deleteUser(id);
        }
        return "redirect:/admin/users";
    }

    // Bookings
    @GetMapping("/bookings")
    public String bookings(Model model) {
        model.addAttribute("bookings", adminService.getAllBookings());
        return "admin-bookings";
    }

    @GetMapping("/bookings/cancel/{id}")
    public String cancelBooking(@PathVariable Long id) {
        adminService.cancelBooking(id);
        return "redirect:/admin/bookings";
    }

    // Payments
    @GetMapping("/payments")
    public String payments(Model model) {
        model.addAttribute("payments", adminService.getAllPayments());
        return "admin-payments";
    }

    // Reports
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("totalBookings", adminService.getTotalBookings());
        model.addAttribute("totalPayments", adminService.getTotalPayments());
        return "admin-reports";
    }
}