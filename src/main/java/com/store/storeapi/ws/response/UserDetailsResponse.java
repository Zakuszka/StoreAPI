package com.store.storeapi.ws.response;

import java.util.List;
import java.util.stream.Collectors;
import com.store.storeapi.pojo.User;

public class UserDetailsResponse {

	private int userId;
	private String username;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String address;
	private List<String> roles;

	public UserDetailsResponse(User user) {
		super();
		this.userId = user.getId();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.phoneNumber = user.getPhoneNumber();
		this.address = user.getAddress();
		this.roles = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList());
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

}
