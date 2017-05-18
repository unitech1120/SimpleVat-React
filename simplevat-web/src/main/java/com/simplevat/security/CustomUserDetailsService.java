package com.simplevat.security;

import com.simplevat.entity.User;
import com.simplevat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailAddress)
            throws UsernameNotFoundException {
        Optional<User> user = userService.getUserByEmail(emailAddress);

        if (user.isPresent()) {
            return new UserContext(user.get());
        } else {
            throw new UsernameNotFoundException("Email not found");
        }
    }
}