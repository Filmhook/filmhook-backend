package com.annular.filmhook.security;

import java.util.Collection;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.annular.filmhook.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String userName;
	private String email;
	private String userType;
	@JsonIgnore
	private String password;

	Set<GrantedAuthority> authorities = null;

	public UserDetailsImpl(Integer id, String userName, String email, String userType, String password) {
		super();
		this.id = id;
		this.userName = userName;
		this.email = email;
		this.userType = userType;
		this.password = password;
	}

	public static UserDetailsImpl build(User user) {
		return new UserDetailsImpl(
				user.getUserId(),
				user.getName(),
				user.getEmail(),
				user.getUserType(),
				user.getPassword()
		);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Integer getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getUserType() {
		return userType;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
