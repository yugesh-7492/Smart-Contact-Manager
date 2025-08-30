package com.practice.smartcontactmanager.controller;



import java.io.File;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.practice.smartcontactmanager.dao.ContactRepository;
import com.practice.smartcontactmanager.dao.UserRepository;
import com.practice.smartcontactmanager.entities.Contact;
import com.practice.smartcontactmanager.entities.User;
import com.practice.smartcontactmanager.helper.Message;

import jakarta.servlet.http.HttpSession;


@Controller


@RequestMapping("/user")

public class UserController {
	
	@Autowired
	
	private UserRepository userRepository;
	
	@Autowired
	
	private ContactRepository contactRepository;
	
	////fetching data  from loged email for showing on the screen it not for ligin or log out  // adding common data to response
	///
	///
	@ModelAttribute
	
	public void addCommonData(Model model ,Principal principal) {
		
		

		String userName=principal.getName();
		
	System.out.println("username : "+userName);
		
//		
		User user=userRepository.getUserByUserName(userName);
		System.out.println("USER : "+user);
		
	// sending data to dashbord  what you want to send  
		
		model.addAttribute("user",user);
		
		
		
		
	}
	
	// for showing in the dasgbord
	
	@RequestMapping("/index")
	public String dashbord(Model model,Principal principal){
		
		
	/// upre wala se same hai 
		
		return "normal/user_dashboard";
		
		
	}
	
	
	
	///////// open add contact handler
	
	
	@RequestMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("tittle", "Add Contact");
		model.addAttribute("contact",new Contact());   // this blank will catch the data from html and store it to contact class
		
		return "normal/add_contact_form";
	}
	
	
	
	// processing contact form
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute("contact") Contact contact,Principal principal,
			
			@RequestParam("profileImage")MultipartFile file,  
			HttpSession session) {  // <--- for image handling , for sending the image we use http session
		
		try {
		
		// fetching user and adding on his contact
		String name=principal.getName();
		User user=this.userRepository.getUserByUserName(name);
		
	
		contact.setUser(user); //adding the user in the contact field
		
		user.getContact().add(contact);  // adding the mapping to user 
		
//		this.userRepository.save(user);  we are using this in the down so its not require here 
		
		
		
		
		// processing and handling image  to the db
		
		if(file.isEmpty()) {
			
			// file is empty then try our message
			
			System.out.print("file is empty");
			
			// optional when he does not upload photo
			
			contact.setImage("icon.png");
		}
		
		else {
			   // file the image to the folder and update the name 
			
			contact.setImage(file.getOriginalFilename());
			
			File saveDir = new File("static/IMAGE");
			if (!saveDir.exists()) saveDir.mkdirs();
			Path path = Paths.get(saveDir.getAbsolutePath(), file.getOriginalFilename());

			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			contactRepository.save(contact);
			
			System.out.print("image is uploaded");
		}
		
		
		// for printion in conole
		
		
		System.out.println("data"+contact);
		System.out.println("added to database");
		
		
		
		// if added then it will show message
		
		session.setAttribute("message", new Message(" your contact is added !! Add more ","success"));
		
		}catch(Exception e){
			
			System.out.println("Error"+e.getMessage());
			e.getStackTrace();
			
			
			// if message error 
			
			session.setAttribute("message", new Message("something went wrong try again ","danger"));
			
		}
		
		return "normal/add_contact_form" ;
		
		
	}
	
	
	// SHOW CONTACT HANDLER
	
	@GetMapping("/show-contact/{page}")
	public String showContact(@PathVariable("page") int page ,Model model,Principal principal) {
		
		model.addAttribute("tittle","Show User Contact");
		
		// for fetching the user
		
		String userName=principal.getName();
		
		User user=this.userRepository.getUserByUserName(userName);
		
		/*
		
		// for paging   current page 5
		
		Pageable pageable = PageRequest.of(page, 5);
		
		// contact ke list ko bhejni hai from user
		
	/*	List<Contact> contacts=this.contactRepository.findContactByUser(user.getId());  initially i used this only
		   
		   
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);

		*/
		
	
		
		// for paging   current page 5
		
		int size = 3;
		Pageable pageable = PageRequest.of(page, size);
		
		
		// contact ke list ko bhejni hai from user

		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);

		
		
		model.addAttribute("contacts",contacts); // sending the contact to the show.html
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());
		
	
		return "normal/show";
	}
	
	
	// showing particular details on the email id
	
	@RequestMapping("/{cId}/contact")
	
	public String showcontactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		
		System.out.println("cid"+cId);
		
		Optional<Contact>contactOptional=this.contactRepository.findById(cId);
		
		Contact contact=contactOptional.get();
		
		// for checking that boy can see the boy linked info not else
		
		String userName=principal.getName();		
		User user=this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId()) {
			
			model.addAttribute("contact",contact);
			
			
		}
		
		
		
		
		return "normal/show_details";
		
		
	}
	
	
	
	
	// delete contact handler
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model, Principal principal, HttpSession session)
	{
		Contact contact = this.contactRepository.findById(cId).get();	

         
		// delete
		
		User user=this.userRepository.getUserByUserName(principal.getName());
		
		user.getContact().remove(contact);
		this.userRepository.save(user);
		
		
	
		return "redirect:/user/show-contact/0";
	}
	
	
	
	// open update form handler
	
	// post can be handle while click not by url
	
	@PostMapping("/update-contact/{cid}")
	
	public String updateForm(Model model,@PathVariable("cid") Integer cid) {
		
		model.addAttribute("tittle","Update contact");
		
		Contact contact = this.contactRepository.findById(cid).get();
		
		model.addAttribute("contact",contact);
		
		return "normal/form_update";
	}
	
	
	// update contact handler
	
	@RequestMapping(value="/process-update",method=RequestMethod.POST)
	
	public String updateHandler(Model model, @ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file,HttpSession session,Principal principal) {
		
		
		
		try {
			
			// old contact details 
			
			Contact oldDetail=this.contactRepository.findById(contact.getcId()).get();
			// image 
			if(!file.isEmpty()) {
				
				// file work
				//rewrite
				
				// delete new photo
				
				File deleteFile=new ClassPathResource("static/IMAGE").getFile();
				File file1=new File(deleteFile,oldDetail.getImage());
				file1.delete();
				
				System.out.println("deleted");
				
				//update new photo
				
				
				File saveDir = new File("static/IMAGE");
				if (!saveDir.exists()) saveDir.mkdirs();
				Path path = Paths.get(saveDir.getAbsolutePath(), file.getOriginalFilename());

				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				
			}else {
				
				contact.setImage(oldDetail.getImage());
				
				
				
				
			}
				
			
			
			User user=this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			
			session.setAttribute("message",new Message("Your Contact is Updated...","success"));
			
			
			
		}catch(Exception e) {
			
			e.printStackTrace();
		}
		
		System.out.println("contact name"+contact.getName());
		System.out.println("contact name"+contact.getcId());
		
		
		return "redirect:/user/"+contact.getcId()+"/contact";
		
		
		
	}
	
	
	// your profile
	
	@GetMapping("/profile")
	public String yourprofile(Model model) {
		
		model.addAttribute("tittle","profile page ");
		
		return "normal/profile";		
		
	}
	
	
}
	


		
	


