package com.megacitycab.cab_service.service;

import com.megacitycab.cab_service.model.*;
import com.megacitycab.cab_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class CustomerService {
    @Autowired private BookingRepository bookingRepository;
    @Autowired private CarRepository carRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DriverRepository driverRepository;

    public List<Booking> getCustomerBookings(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            Customer customer = customerRepository.findByUser(user);
            return bookingRepository.findByCustomer(customer);
        }
        return List.of();
    }

    public void cancelBooking(Long id, String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            Customer customer = customerRepository.findByUser(user);
            Booking booking = bookingRepository.findById(id).orElse(null);
            if (booking != null && booking.getCustomer().equals(customer) && !"Cancelled".equals(booking.getStatus())) {
                booking.setStatus("Cancelled");
                bookingRepository.save(booking);
            }
        }
    }

    public void createBooking(Booking booking, String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            Customer customer = customerRepository.findByUser(user);
            booking.setCustomer(customer); // Still set here for consistency, though pre-set in controller
            if (booking.getDateTime() == null) {
                booking.setDateTime(LocalDateTime.now());
            }
            booking.setBookingNumber(generateBookingNumber());
            booking.setStatus("Pending");
            bookingRepository.save(booking);
            Driver driver = booking.getDriver();
            if (driver != null) {
                driver.setAvailable(false);
                driverRepository.save(driver);
            }
        }
    }

    private String generateBookingNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "BOOK-" + timestamp + "-" + random;
    }

    public List<Car> getAvailableCars() {
        return carRepository.findAll().stream().filter(Car::isAvailable).toList();
    }

    public List<Driver> getAvailableDrivers() {
        return driverRepository.findByAvailableTrue();
    }

    public Booking getBooking(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public void processMockPayment(Long bookingId, double amount, String paymentDate, String username) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null && "Pending".equals(booking.getStatus())) {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                Customer customer = customerRepository.findByUser(user);
                if (booking.getCustomer().equals(customer)) {
                    Payment payment = new Payment();
                    payment.setBooking(booking);
                    payment.setAmount(amount);
                    payment.setPaymentDate(paymentDate);
                    paymentRepository.save(payment);
                    booking.setStatus("Completed");
                    bookingRepository.save(booking);
                    Driver driver = booking.getDriver();
                    if (driver != null) {
                        driver.setAvailable(true);
                        driverRepository.save(driver);
                    }
                }
            }
        }
    }

    public List<Payment> getCustomerPayments(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            Customer customer = customerRepository.findByUser(user);
            return paymentRepository.findAll().stream()
                    .filter(payment -> payment.getBooking().getCustomer().equals(customer))
                    .toList();
        }
        return List.of();
    }

    public Customer getCustomerByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return customerRepository.findByUser(user);
        }
        return null;
    }
}