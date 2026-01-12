package com.annular.filmhook;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.annular.filmhook.model.AdminUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AdminUserDetails implements UserDetails {

    private Integer id;
    private String email;

    @JsonIgnore
    private String password;

    private String role;

    // ✅ ADD THIS CONSTRUCTOR
    public AdminUserDetails(Integer id, String email, String password, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static AdminUserDetails build(AdminUser admin) {
        return new AdminUserDetails(
                admin.getId(),
                admin.getEmail(),
                admin.getPassword(),
                admin.getRole().getRoleCode()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public Integer getId() {
        return id;
    }

    public String getRole() {
        return role;
    }
}