package com.practice.smartcontactmanager.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.practice.smartcontactmanager.entities.Contact;

public interface ContactRepository extends JpaRepository <Contact ,Integer>{
	
	// pagination for getting the contact from user
	
	
	//@Query("from Contact as c where c.user.id=:userId")  // while finding it will ask userid for printing all contact that belongs from the particular user
//	public List<Contact> findContactByUser(@Param("userId")int UserId);
	
	
	
	// pagination next to next
	//public Page<Contact> findContactByUser(@Param("userId")int UserId, Pageable pageable);

	@Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
	Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);


}
