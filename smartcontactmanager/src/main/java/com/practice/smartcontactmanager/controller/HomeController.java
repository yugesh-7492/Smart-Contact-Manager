package com.practice.smartcontactmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.practice.smartcontactmanager.dao.UserRepository;
import com.practice.smartcontactmanager.entities.User;
import com.practice.smartcontactmanager.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	
//	@Autowired

//    private BCryptPasswordEncoder passwordEncoder  ;
	
	@Autowired

    private  UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Home page
    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    // About page
    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    // Signup page
    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    // Register user handler
    
    @PostMapping("/do_register")
    public String registerUser(@Valid  @ModelAttribute("user") User user, BindingResult result1,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
            Model model,
            HttpSession session) {

        try {
            if (!agreement) {
                throw new Exception("You have not agreed to the terms and conditions.");
            }

            
            if(result1.hasErrors()) {
            	
            	System.out.println("error"+result1.toString());
            	model.addAttribute("user",user);
            	return "signup";
            }
            
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            
            user.setImageUrl("default.png");
            
            
           

            User result = userRepository.save(user);

            model.addAttribute("user", new User()); // Clear the form
            
            session.setAttribute("message", new Message("Successfully registered!", "alert-success"));

            return "signup";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
            return "signup";
       
      }
        
        // handler for custome login 
        
        
    }
    
    @GetMapping("/signin")
    public String customLogin(Model model) {
    	
    	model.addAttribute("tittle","login page");
    	
		return "login";
    	
    	
    }
    
}
