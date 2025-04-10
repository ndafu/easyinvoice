package com.luna.EasyInvoice.service.implementation;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.User;
import com.luna.EasyInvoice.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	com.luna.EasyInvoice.repository.UserRepository userrepo;
	
	@Override
	public User saveUser(User user) {
		User us = new User();
		if(user!=null) {
			us= userrepo.save(user);
		}
		return us;
	}

	@Override
	public Page<User> fetchUsers(int pageno, int pagesize) {
		Pageable pageable = PageRequest.of(pageno -1,pagesize);
		UserPrincipal princ = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
		System.out.println(princ.getAuthorities());
		if (princ != null && princ.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
			return this.userrepo.findAll(pageable);
		}else {
			return this.userrepo.findByOrganisation(pageable);
		}
	}

	@Override
	public User finduserbyid(int id) {
		Optional<User> user = this.userrepo.findById(Long.valueOf(id));
		if(user.isPresent())
		return user.get();
		else return null;
	}

	@Override
	public User findeuserbyUsername(String username) {
		User user = new User();
		if(!username.isEmpty()) {
			user = this.userrepo.findByUsername(username);
		}
		return user;
	}
}
