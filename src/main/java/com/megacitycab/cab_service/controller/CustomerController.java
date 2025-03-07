package com.megacitycab.cab_service.controller;

import com.megacitycab.cab_service.model.*;
import com.megacitycab.cab_service.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Random;

@Controller
@RequestMapping("/customer")
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    // Pre-populate Booking with customer before binding
    @ModelAttribute("booking")
    public Booking prePopulateBooking(Principal principal) {
        Booking booking = new Booking();
        String username = principal.getName();
        Customer customer = customerService.getCustomerByUsername(username);
        if (customer != null) {
            booking.setCustomer(customer);
        }
        booking.setDateTime(LocalDateTime.now());
        return booking;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        String username = principal.getName();
        model.addAttribute("username", username);
        return "customer-dashboard";
    }

    @GetMapping("/bookings")
    public String viewBookings(Principal principal, Model model) {
        String username = principal.getName();
        model.addAttribute("bookings", customerService.getCustomerBookings(username));
        return "customer-bookings";
    }

    @GetMapping("/bookings/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, Principal principal) {
        String username = principal.getName();
        customerService.cancelBooking(id, username);
        return "redirect:/customer/bookings";
    }

    @GetMapping("/make-booking")
    public String showDetailedBookingForm(Model model) {
        model.addAttribute("cars", customerService.getAvailableCars());
        model.addAttribute("drivers", customerService.getAvailableDrivers());
        return "customer-detailed-booking";
    }

    @PostMapping("/make-booking")
    public String makeBooking(@Valid @ModelAttribute Booking booking, BindingResult result,
                              @RequestParam(required = false) String payNow, Principal principal, Model model) {
        String username = principal.getName();
        Customer customer = customerService.getCustomerByUsername(username);
        if (customer == null) {
            model.addAttribute("error", "User not associated with a customer profile.");
            model.addAttribute("cars", customerService.getAvailableCars());
            model.addAttribute("drivers", customerService.getAvailableDrivers());
            return "customer-detailed-booking";
        }

        if (result.hasErrors()) {
            System.out.println("******************************************************");
            System.out.println("******************************************************");
            System.out.println(result);
            System.out.println("******************************************************");
            System.out.println("******************************************************");
            model.addAttribute("cars", customerService.getAvailableCars());
            model.addAttribute("drivers", customerService.getAvailableDrivers());
            return "customer-detailed-booking";
        }

        booking.setBookingNumber(generateBookingNumber());
        booking.setStatus("Pending");
        if (booking.getDateTime() == null) {
            booking.setDateTime(LocalDateTime.now());
        }
        booking.setTotalAmount(calculateTotalAmount(booking.getFromLocation(), booking.getDestination()));
        customerService.createBooking(booking, username);

        if (payNow != null && payNow.equals("on")) {
            return "redirect:/customer/payment?id=" + booking.getId();
        }
        return "redirect:/customer/bookings";
    }

    @GetMapping("/payment")
    public String showPaymentForm(@RequestParam("id") Long id, Model model) {
        Booking booking = customerService.getBooking(id);
        if (booking == null || !booking.getStatus().equals("Pending")) {
            return "redirect:/customer/bookings";
        }
        model.addAttribute("booking", booking);
        return "customer-payment";
    }

    @PostMapping("/payment")
    public String processPayment(@RequestParam Long bookingId, Principal principal) {
        String username = principal.getName();
        Booking booking = customerService.getBooking(bookingId);
        if (booking != null && booking.getStatus().equals("Pending")) {
            double amount = booking.getTotalAmount();
            String paymentDate = java.time.LocalDate.now().toString();
            customerService.processMockPayment(bookingId, amount, paymentDate, username);
        }
        return "redirect:/customer/payment-history";
    }

    @GetMapping("/payment-history")
    public String viewPaymentHistory(Principal principal, Model model) {
        String username = principal.getName();
        model.addAttribute("payments", customerService.getCustomerPayments(username));
        return "customer-payment-history";
    }

    private String generateBookingNumber() {
        Random random = new Random();
        return "BK" + String.format("%06d", random.nextInt(1000000));
    }

    private double calculateTotalAmount(String fromLocation, String destination) {
        int distanceKm = estimateDistance(fromLocation, destination);
        return 10.0 + (distanceKm / 10) * 5.0;
    }

    private int estimateDistance(String fromLocation, String destination) {
        if (fromLocation == null || destination == null) return 15;
        String fromLower = fromLocation.toLowerCase();
        String destLower = destination.toLowerCase();
        if (fromLower.equals("city center") && destLower.equals("airport")) return 30;
        if (fromLower.equals("airport") && destLower.equals("city center")) return 30;
        if (fromLower.equals("city center") && destLower.equals("suburbs")) return 20;
        if (fromLower.equals("suburbs") && destLower.equals("city center")) return 20;
        if (fromLower.equals("airport") && destLower.equals("suburbs")) return 40;
        if (fromLower.equals("suburbs") && destLower.equals("airport")) return 40;
        return 15;
    }
}