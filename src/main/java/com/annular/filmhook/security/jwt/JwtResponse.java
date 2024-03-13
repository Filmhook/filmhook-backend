package com.annular.filmhook.security.jwt;

public class JwtResponse {

	private String jwt;
	private Integer id;
	private String username;
	private String email;
	private String message;
	private Integer status;
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public JwtResponse(String jwt, Integer id, String username, String email, String message, Integer status,
			String token) {
		super();
		this.jwt = jwt;
		this.id = id;
		this.username = username;
		this.email = email;
		this.message = message;
		this.status = status;
		this.token = token;
	}

}
