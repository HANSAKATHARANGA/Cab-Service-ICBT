package com.megacitycab.cab_service.service;

import com.megacitycab.cab_service.model.*;
import com.megacitycab.cab_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminService {
    @Autowired private CarRepository carRepository;
    @Autowired private DriverRepository driverRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // Check if username exists
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // Car CRUD
    public Car createCar(Car car) { return carRepository.save(car); }
    public List<Car> getAllCars() { return carRepository.findAll(); }
    public Car getCar(Long id) { return carRepository.findById(id).orElse(null); }
    public Car updateCar(Car car) { return carRepository.save(car); }
    public void deleteCar(Long id) { carRepository.deleteById(id); }

    // Driver CRUD
    public Driver createDriver(Driver driver) { return driverRepository.save(driver); }
    public List<Driver> getAllDrivers() { return driverRepository.findAll(); }
    public Driver getDriver(Long id) { return driverRepository.findById(id).orElse(null); }
    public Driver updateDriver(Driver driver) { return driverRepository.save(driver); }
    public void deleteDriver(Long id) { driverRepository.deleteById(id); }

    // User CRUD
    public User createUser(User user, Customer customer, String roleName) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByName("ROLE_" + roleName.toUpperCase());
        if (role == null) {
            role = new Role("ROLE_" + roleName.toUpperCase());
            roleRepository.save(role);
        }
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        if (customer != null && roleName.equalsIgnoreCase("customer")) {
            customer.setUser(savedUser);
            customerRepository.save(customer);
        }
        return savedUser;
    }
    public List<User> getAllUsers() { return userRepository.findAll(); }
    public User getUser(Long id) { return userRepository.findById(id).orElse(null); }
    public User updateUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public void deleteUser(Long id) { userRepository.deleteById(id); }

    // Bookings
    public List<Booking> getAllBookings() { return bookingRepository.findAll(); }
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking != null) {
            booking.setStatus("Cancelled");
            bookingRepository.save(booking);
        }
    }

    // Payments
    public List<Payment> getAllPayments() { return paymentRepository.findAll(); }

    // Reports
    public long getTotalBookings() { return bookingRepository.count(); }
    public double getTotalPayments() {
        return paymentRepository.findAll().stream().mapToDouble(Payment::getAmount).sum();
    }
}