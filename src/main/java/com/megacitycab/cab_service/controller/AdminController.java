package com.megacitycab.cab_service.controller;

import com.megacitycab.cab_service.model.*;
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
    public String createUser(@Valid @ModelAttribute User user, @Valid @ModelAttribute Customer customer,
                             @RequestParam String role, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", adminService.getAllUsers());
            return "admin-users";
        }
        adminService.createUser(user, role.equalsIgnoreCase("customer") ? customer : null, role);
        return "redirect:/admin/users";
    }
    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", adminService.getUser(id));
        model.addAttribute("customer", new Customer());
        model.addAttribute("users", adminService.getAllUsers());
        return "admin-users";
    }
    @PostMapping("/users/update")
    public String updateUser(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", adminService.getAllUsers());
            return "admin-users";
        }
        adminService.updateUser(user);
        return "redirect:/admin/users";
    }
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
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