package com.annular.filmhook.security.jwt;

public class JwtResponse {

	private String jwt;
	private Integer id;
	private String username;
	private String email;
	private String message;
	private Integer status;
	private String token;
	private String userType;
	private String filmHookCode;
	private String firstName;
	private String lastName;
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getFilmHookCode() {
		return filmHookCode;
	}
	public void setFilmHookCode(String filmHookCode) {
		this.filmHookCode = filmHookCode;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @param jwt
	 * @param id
	 * @param username
	 * @param email
	 * @param message
	 * @param status
	 * @param token
	 * @param userType
	 * @param filmHookCode
	 * @param firstName
	 * @param lastName
	 */
	public JwtResponse(String jwt, Integer id, String username, String email, String message, Integer status,
			String token, String userType, String filmHookCode, String firstName, String lastName) {
		super();
		this.jwt = jwt;
		this.id = id;
		this.username = username;
		this.email = email;
		this.message = message;
		this.status = status;
		this.token = token;
		this.userType = userType;
		this.filmHookCode = filmHookCode;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	

}
