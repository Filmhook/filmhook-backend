package com.annular.filmhook.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.annular.filmhook.UserStatusConfig;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.security.UserDetailsImpl;

@Service
@Qualifier("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    public static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private static final String CARET = "^";
    private static final String ESCAPED_CARET = "\\^";

    @Autowired
    UserRepository userRepo;

//	JwtUtils jwtUtils;

    @Autowired
    UserStatusConfig loginConstants;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("I am from loadUserByUsername() !!! ");
        logger.info("Email :- {}, UserType from LoginConstants :- {}", email, loginConstants.getUserType());
        email = email.contains(CARET) ? email.split(ESCAPED_CARET)[0] : email;
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            logger.info("User from DB --> {} -- {} -- {}", user.getUserId(), user.getEmail(), user.getUserType());
            return UserDetailsImpl.build(user);
        } else {
            throw new UsernameNotFoundException("User Not Found with email: " + email);
        }
    }

}