package com.coursemanagement.security.config;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new SystemException("Cannot find user by email: " + username, SystemErrorCode.INTERNAL_SERVER_ERROR));
    }
}
