package com.intech.evintechaccount.models;

import java.util.ArrayList;
import java.util.List;

public class Comment {
	
	private int id;
	private User user;
	private String content;
	private String date;
	private List<Comment> responses = new ArrayList<Comment>();
	private boolean response = false;
	private int idParent = 0;
	
	public Comment(String content) {
		this.content = content;
	}
	
	public Comment() {
		
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Comment> getResponses() {
		return responses;
	}

	public void setResponses(List<Comment> responses) {
		this.responses = responses;
	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public int getIdParent() {
		return idParent;
	}

	public void setIdParent(int idParent) {
		this.idParent = idParent;
	}

}
