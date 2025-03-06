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
@RequestMapping("/moderator")
@PreAuthorize("hasRole('ROLE_MODERATOR')")
public class ModeratorController {
    @Autowired private AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "moderator-dashboard";
    }

    // Cars
    @GetMapping("/cars")
    public String cars(Model model) {
        model.addAttribute("cars", adminService.getAllCars());
        model.addAttribute("car", new Car());
        return "moderator-cars";
    }
    @PostMapping("/cars/create")
    public String createCar(@Valid @ModelAttribute Car car, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("cars", adminService.getAllCars());
            return "moderator-cars";
        }
        adminService.createCar(car);
        return "redirect:/moderator/cars";
    }
    @GetMapping("/cars/edit/{id}")
    public String editCar(@PathVariable Long id, Model model) {
        model.addAttribute("car", adminService.getCar(id));
        model.addAttribute("cars", adminService.getAllCars());
        return "moderator-cars";
    }
    @PostMapping("/cars/update")
    public String updateCar(@Valid @ModelAttribute Car car, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("cars", adminService.getAllCars());
            return "moderator-cars";
        }
        adminService.updateCar(car);
        return "redirect:/moderator/cars";
    }
    @GetMapping("/cars/delete/{id}")
    public String deleteCar(@PathVariable Long id) {
        adminService.deleteCar(id);
        return "redirect:/moderator/cars";
    }

    // Drivers
    @GetMapping("/drivers")
    public String drivers(Model model) {
        model.addAttribute("drivers", adminService.getAllDrivers());
        model.addAttribute("driver", new Driver());
        model.addAttribute("cars", adminService.getAllCars());
        return "moderator-drivers";
    }
    @PostMapping("/drivers/create")
    public String createDriver(@Valid @ModelAttribute Driver driver, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("drivers", adminService.getAllDrivers());
            model.addAttribute("cars", adminService.getAllCars());
            return "moderator-drivers";
        }
        adminService.createDriver(driver);
        return "redirect:/moderator/drivers";
    }
    @GetMapping("/drivers/edit/{id}")
    public String editDriver(@PathVariable Long id, Model model) {
        model.addAttribute("driver", adminService.getDriver(id));
        model.addAttribute("drivers", adminService.getAllDrivers());
        model.addAttribute("cars", adminService.getAllCars());
        return "moderator-drivers";
    }
    @PostMapping("/drivers/update")
    public String updateDriver(@Valid @ModelAttribute Driver driver, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("drivers", adminService.getAllDrivers());
            model.addAttribute("cars", adminService.getAllCars());
            return "moderator-drivers";
        }
        adminService.updateDriver(driver);
        return "redirect:/moderator/drivers";
    }
    @GetMapping("/drivers/delete/{id}")
    public String deleteDriver(@PathVariable Long id) {
        adminService.deleteDriver(id);
        return "redirect:/moderator/drivers";
    }

    // Bookings
    @GetMapping("/bookings")
    public String bookings(Model model) {
        model.addAttribute("bookings", adminService.getAllBookings());
        return "moderator-bookings";
    }
    @GetMapping("/bookings/cancel/{id}")
    public String cancelBooking(@PathVariable Long id) {
        adminService.cancelBooking(id);
        return "redirect:/moderator/bookings";
    }
}