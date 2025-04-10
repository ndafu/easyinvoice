package com.luna.EasyInvoice.service;


import org.springframework.data.domain.Page;

import com.luna.EasyInvoice.entities.User;

public interface UserService {
	public User saveUser(User user);
	Page<User> fetchUsers(int pageno, int pagesize);
	User finduserbyid(int id);
	User findeuserbyUsername(String username);
}
