package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.contactRepository;
import com.smart.dao.userRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
@RequestMapping("/user")
public class SearchController {

	@Autowired
	private userRepository userRepository;

	@Autowired
	private contactRepository contactRepository;

	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(Principal principal, @PathVariable("query") String query) {
		User user = this.userRepository.getUserByUserName(principal.getName());
		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}
}