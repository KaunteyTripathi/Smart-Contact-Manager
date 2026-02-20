package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Helper.Message;
import com.smart.dao.userRepository;
import com.smart.entities.User;

import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private userRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Home page
	@GetMapping("/")
	public String home() {
		return "home";
	}

	@GetMapping("/base")
	public String base() {
		return "base";
	}

	// Login page
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	// about page
	@GetMapping("/about")
	public String about() {
		return "about";
	}

	// Signup page
	@GetMapping("/sign")
	public String signup(Model model) {
		model.addAttribute("user", new User());
		return "sign";
	}

	// Handle registration
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
			RedirectAttributes redirectAttributes, Model model) {

		if (!agreement) {
			redirectAttributes.addFlashAttribute("message",
					new Message("You must accept terms and conditions", "alert-danger"));
			return "redirect:/sign";
		}

		// Check validation errors
		if (result.hasErrors()) {
			model.addAttribute("user", user);
			return "sign";
		}

		// Encode password before saving
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// Assign default role USER
		user.setRole("USER");

		// Save user
		userRepository.save(user);

		redirectAttributes.addFlashAttribute("message", new Message("Successfully registered", "alert-success"));

		return "redirect:/login"; // redirect to login page after registration
	}
}
