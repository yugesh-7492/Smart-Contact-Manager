package com.practice.smartcontactmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.practice.smartcontactmanager.dao.UserRepository;
import com.practice.smartcontactmanager.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		
		
		// how to fetch user from database 
		
		User user=userRepository.getUserByUserName(username);
		
		if(user==null) {
			
			throw new UsernameNotFoundException("could not found user");
			
			
		}
		
		 CustomUserDetails  customUserDetails=new  CustomUserDetails(user);
		
		return  customUserDetails;
		
	}

}
