package com.vrg.payserver.dao.model;

public class ChannelParam {
	private int id;
	private String name;
	private String clientVisible;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClientVisible() {
		return clientVisible;
	}

	public void setClientVisible(String clientVisible) {
		this.clientVisible = clientVisible;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
