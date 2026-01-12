package com.annular.filmhook.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.annular.filmhook.AdminUserDetails;
import com.annular.filmhook.model.AdminUser;
import com.annular.filmhook.repository.AdminUserRepository;
@Service
@Qualifier("adminUserDetailsService")
public class AdminDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminUserRepository adminRepo;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        AdminUser admin = adminRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Admin not found with email: " + email));

        return AdminUserDetails.build(admin);
    }
}