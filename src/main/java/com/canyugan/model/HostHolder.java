package com.canyugan.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder 
{
	//为每个线程分配独立的内存
	//同一条线程可以共享
	private static ThreadLocal<User> users = new ThreadLocal<User>();
	
	public User getUser() {
		return users.get();
	}
	
	public void setUser(User user) {
		users.set(user);
	}
	
	public void clear()
	{
		users.remove();
	}
}
