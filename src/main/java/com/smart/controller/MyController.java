package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.userRepository;
import com.smart.entities.User;

@Controller
public class MyController {
	@Autowired
	private userRepository UserRepository;

	@GetMapping("/test")
	@ResponseBody

	private String test() {

		User user = new User();

		user.setName("Kauntey Tripathi");
		UserRepository.save(user);
		return "working";

	}
}
