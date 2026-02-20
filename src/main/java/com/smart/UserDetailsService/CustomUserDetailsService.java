package com.smart.UserDetailsService;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smart.dao.userRepository;
import com.smart.entities.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final userRepository userRepository;

    public CustomUserDetailsService(userRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.getUserByUserName(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Assign role properly
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority) // roles/authorities
        );
    }
}
