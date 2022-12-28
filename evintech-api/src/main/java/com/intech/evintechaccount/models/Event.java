package com.intech.evintechaccount.models;

import java.util.ArrayList;
import java.util.List;

public class Event {
	
	private int id;
	
	private String eventName;
	private String category;
	
	private String urlImage;
	
	private String description;
	private int price;
	
	private User creator;
	
	private String dateCreation;
	private String dateCloseEvent;
	private String dateEvent;
	
	private List<User> like = new ArrayList<User>();
	private List<Comment> comments = new ArrayList<Comment>();
	private List<User> participants = new ArrayList<User>();
	
	public Event(User creator, String eventName, String category, String urlImage, String description, int price) {
		
		this.eventName = eventName;
		this.category = category;
		this.urlImage = urlImage;
		this.description = description;
		this.price = price;
		this.creator = creator;
	}
	
	public Event(User creator, String eventName, String category, String urlImage, String description, String dateCloseEvent,
			String dateEvent) {
		
		this.eventName = eventName;
		this.category = category;
		this.urlImage = urlImage;
		this.description = description;
		price = 0;
		this.creator = creator;
		this.dateCloseEvent = dateCloseEvent;
		this.dateEvent = dateEvent;
		
	}
	
	public Event() {
		
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getUrlImage() {
		return urlImage;
	}

	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<User> getLike() {
		return like;
	}
	
	public void addLike(User user) {
		like.add(user);
	}
	
	public void removeLike(User user) {
		participants.remove(user);
	}

	public void setLike(List<User> like) {
		this.like = like;
	}

	public List<Comment> getComments() {
		return comments;
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
	}
	
	public void removeComment(Comment comment) {
		comments.remove(comment);
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<User> getParticipants() {
		return participants;
	}
	
	public void addParticipant(User user) {
		participants.add(user);
	}
	
	public void removeParticipant(User user) {
			participants.remove(user);
	}

	public void setParticipants(List<User> participants) {
		this.participants = participants;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public String getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDateCloseEvent() {
		return dateCloseEvent;
	}

	public void setDateCloseEvent(String dateCloseEvent) {
		this.dateCloseEvent = dateCloseEvent;
	}

	public String getDateEvent() {
		return dateEvent;
	}

	public void setDateEvent(String dateEvent) {
		this.dateEvent = dateEvent;
	}
	
}
