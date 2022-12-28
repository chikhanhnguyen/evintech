package com.intech.evintechaccount.models;

import java.util.List;

public class User{
	
	private String biography;
	private String email;
	private List<Event> eventFavorites;
	private String password;
	private String role;
	private String profilePicture;
	private String userFirstName;
	private String userId;
	private String userName;

	public User(String biography, String email, List<Event> eventFavorites, String password, String role,
			String profilePicture, String userFirstName, String userId, String userName) {
		this.userId = userId;
		this.userName = userName;
		this.userFirstName = userFirstName;
		this.email = email;
		this.password = password;
		this.profilePicture = profilePicture;
		this.biography = biography;
		this.role = role;
		this.eventFavorites = eventFavorites;
	}

	public User() {
		
	}
	
	public String getBiography() {
		return biography;
	}

	public void setBiography(String biography) {
		this.biography = biography;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public List<Event> getEventFavorites() {
		return eventFavorites;
	}

	public void setEventFavorites(List<Event> eventFavorites) {
		this.eventFavorites = eventFavorites;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public String getProfilePicture() {
		return profilePicture;
	}

	public void setUrlProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	
	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
