package com.practice.smartcontactmanager.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.practice.smartcontactmanager.dao.UserRepository;
import com.practice.smartcontactmanager.entities.User;

import jakarta.validation.constraints.Email;

@Controller
public class AdminController {

  
	
	@Autowired
	private UserRepository userRepository;


	
	/*
	// no need to use here principal 
	
	@GetMapping("/admin/index")
	
	public String showContact(Model model, Principal principal) {
	    // fetch all users
	    model.addAttribute("users", userRepository.findAll());
	    return "admin/admin_pannel";
	}
	*/
	
	// for searchin implementation
	

	@GetMapping("/admin/index")
	public String viewUsers(@RequestParam(value = "email", required = false) String email,
	                        Model model) {
	    if (email != null && !email.trim().isEmpty()) {
	        // 🔍 search users by email
	        model.addAttribute("users", userRepository.findByEmailContainingIgnoreCase(email));
	    } else {
	        // 📋 show all users
	        model.addAttribute("users", userRepository.findAll());
	    }

	    model.addAttribute("email", email); // keep search box filled
	    return "admin/admin_pannel";  // 👈 your Thymeleaf template name
	}
	
	
	// delete the contact 
	
	@GetMapping("/admin/delete/{email}")
	public String deleteUser(@PathVariable("email") String email) {
	    // extra safety: don't allow admin to delete themselves
	    User user = userRepository.getUserByUserName(email);
	    if (user != null && !"ROLE_ADMIN".equals(user.getRole())) {
	        userRepository.delete(user);  // delete user entity
	    }
	    return "redirect:/admin/index"; // redirect back to user list
	}



}
