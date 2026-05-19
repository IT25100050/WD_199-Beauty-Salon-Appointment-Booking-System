package com.beautysalon.auth.service;

import com.beautysalon.admin.entity.Admin;
import com.beautysalon.admin.repository.AdminRepository;
import com.beautysalon.auth.dto.ForgotPasswordRequest;
import com.beautysalon.auth.dto.LoginRequest;
import com.beautysalon.auth.dto.LoginResponse;
import com.beautysalon.auth.dto.RegisterRequest;
import com.beautysalon.auth.entity.User;
import com.beautysalon.auth.exception.DuplicateUserException;
import com.beautysalon.auth.exception.InvalidCredentialsException;
import com.beautysalon.auth.repository.UserRepository;
import com.beautysalon.customer.entity.Customer;
import com.beautysalon.customer.repository.CustomerRepository;
import com.beautysalon.staff.entity.Staff;
import com.beautysalon.staff.repository.StaffRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       CustomerRepository customerRepository,
                       AdminRepository adminRepository,
                       StaffRepository staffRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public String register(RegisterRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();
        String role = request.getRole().toUpperCase();

        // Check uniqueness across all user lists
        if (userRepository.findByUsername(username).isPresent() ||
            adminRepository.findByUsername(username).isPresent() ||
            staffRepository.findByUsername(username).isPresent()) {
            throw new DuplicateUserException("Username is already taken");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateUserException("Email is already registered");
        }

        String id = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        if (role.equals("ADMIN")) {
            Admin admin = new Admin(id, username, encodedPassword, email, "ADMIN", request.isSuperAdmin());
            adminRepository.save(admin);
        } else if (role.equals("STAFF")) {
            Staff staff = new Staff(id, username, encodedPassword, email, "STAFF",
                    request.getSpecialization(), request.getSchedule(), true);
            staffRepository.save(staff);
        } else {
            // Default to CUSTOMER
            Customer customer = new Customer(id, username, encodedPassword, email, "CUSTOMER",
                    request.getPhoneNumber(), request.getAddress());
            customerRepository.save(customer);
        }

        return "User registered successfully with role: " + role;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String username = request.getUsername();
            String role = "";
            String userId = "";

            Optional<Admin> admin = adminRepository.findByUsername(username);
            if (admin.isPresent()) {
                role = "ADMIN";
                userId = admin.get().getId();
            } else {
                Optional<Staff> staff = staffRepository.findByUsername(username);
                if (staff.isPresent()) {
                    role = "STAFF";
                    userId = staff.get().getId();
                } else {
                    Optional<User> user = userRepository.findByUsername(username);
                    if (user.isPresent()) {
                        role = "CUSTOMER";
                        userId = user.get().getId();
                    }
                }
            }

            return new LoginResponse(userId, username, role, "Login successful");
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    public String forgotPassword(ForgotPasswordRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();
        String newPasswordEncoded = passwordEncoder.encode(request.getNewPassword());

        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent() && adminOpt.get().getEmail().equalsIgnoreCase(email)) {
            Admin admin = adminOpt.get();
            admin.setPassword(newPasswordEncoded);
            adminRepository.update(admin);
            return "Password reset successfully for Admin";
        }

        Optional<Staff> staffOpt = staffRepository.findByUsername(username);
        if (staffOpt.isPresent() && staffOpt.get().getEmail().equalsIgnoreCase(email)) {
            Staff staff = staffOpt.get();
            staff.setPassword(newPasswordEncoded);
            staffRepository.update(staff);
            return "Password reset successfully for Staff";
        }

        Optional<Customer> customerOpt = customerRepository.findByUsername(username);
        if (customerOpt.isPresent() && customerOpt.get().getEmail().equalsIgnoreCase(email)) {
            Customer customer = customerOpt.get();
            customer.setPassword(newPasswordEncoded);
            customerRepository.update(customer);
            return "Password reset successfully for Customer";
        }

        throw new InvalidCredentialsException("Invalid username and email combination");
    }
}
