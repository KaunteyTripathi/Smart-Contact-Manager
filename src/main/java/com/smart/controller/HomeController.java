package com.smart.controller;

import java.time.LocalDateTime;
import java.util.UUID;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.UserDetailsService.emailService;
import com.smart.dao.TokenRepository;
import com.smart.dao.userRepository;
import com.smart.entities.Token;
import com.smart.entities.User;

import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private userRepository userRepository;
	@Autowired
	private emailService EmailService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired

	private TokenRepository tokenRepository;

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
			RedirectAttributes redirectAttributes, Model model) throws Exception {

		if (!agreement) {
			redirectAttributes.addFlashAttribute("message",
					new Message("You must accept terms and conditions", "alert-danger"));
			return "redirect:/sign";
		}

		if (result.hasErrors()) {
			model.addAttribute("user", user);
			return "sign";
		}

		// Check duplicate email
		if (userRepository.getUserByUserName(user.getEmail()) != null) {
			redirectAttributes.addFlashAttribute("message", new Message("Email already registered", "alert-danger"));
			return "redirect:/sign";
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole("USER");

		// Generate token
		String tokenValue = UUID.randomUUID().toString();

		ObjectMapper mapper = new ObjectMapper();
		String userJson = mapper.writeValueAsString(user);

		Token token = new Token();
		token.setToken(tokenValue);
		token.setUserData(userJson);
		token.setExpiryTime(LocalDateTime.now().plusMinutes(15));

		tokenRepository.save(token);

		String link = "http://localhost:8282/verify-signup?token=" + tokenValue;

		EmailService.sendEmail("Click this link to verify your account: " + link, user.getEmail(), "Verify Your Email");

		redirectAttributes.addFlashAttribute("message",
				new Message("Verification link sent to your email", "alert-success"));

		return "redirect:/sign";
	}

	@GetMapping("/verify-signup")
	public String verifySignup(@RequestParam("token") String tokenValue, RedirectAttributes redirectAttributes)
			throws Exception {

		Token token = tokenRepository.findByToken(tokenValue);

		if (token == null || token.getExpiryTime().isBefore(LocalDateTime.now())) {

			redirectAttributes.addFlashAttribute("message", new Message("Invalid or expired token", "alert-danger"));
			return "redirect:/sign";
		}

		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(token.getUserData(), User.class);

		userRepository.save(user);

		tokenRepository.delete(token);

		redirectAttributes.addFlashAttribute("message", new Message("Account verified successfully!", "alert-success"));

		return "redirect:/login";
	}
}
