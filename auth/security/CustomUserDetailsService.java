package com.beautysalon.auth.security;

import com.beautysalon.admin.entity.Admin;
import com.beautysalon.admin.repository.AdminRepository;
import com.beautysalon.auth.entity.User;
import com.beautysalon.auth.repository.UserRepository;
import com.beautysalon.staff.entity.Staff;
import com.beautysalon.staff.repository.StaffRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final StaffRepository staffRepository;

    public CustomUserDetailsService(UserRepository userRepository, AdminRepository adminRepository, StaffRepository staffRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.staffRepository = staffRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try Admin
        Optional<Admin> admin = adminRepository.findByUsername(username);
        if (admin.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(admin.get().getUsername())
                    .password(admin.get().getPassword())
                    .roles(admin.get().getRole()) // e.g. "ADMIN"
                    .build();
        }

        // Try Staff
        Optional<Staff> staff = staffRepository.findByUsername(username);
        if (staff.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(staff.get().getUsername())
                    .password(staff.get().getPassword())
                    .roles(staff.get().getRole()) // e.g. "STAFF"
                    .build();
        }

        // Try User/Customer
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.get().getUsername())
                    .password(user.get().getPassword())
                    .roles(user.get().getRole()) // e.g. "CUSTOMER"
                    .build();
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
