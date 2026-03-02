package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.UserDetailsService.emailService;
import com.smart.dao.userRepository;
import com.smart.entities.User;

import jakarta.servlet.http.HttpSession;

@Controller

public class MyController {
	@Autowired
	private userRepository UserRepository;
	@Autowired
	private BCryptPasswordEncoder bcryptpasswordEncoder;
	@Autowired
	private emailService EmailService;
	Random random = new Random(1001);

	@GetMapping("/forgot")
	public String openForgotPasswordPage() {
		return "forgot"; // forgot.html
	}

	@PostMapping("/send-otp")
	public String sendotp(@RequestParam("username") String username, HttpSession session, Model model) {

		// 🔍 Step 1: Check if user exists
		User user = UserRepository.getUserByUserName(username);

		if (user == null) {
			// Email not found → stay on forgot page
			model.addAttribute("error", "Email not registered!");
			return "forgot";
		}

		// 🔐 Step 2: Generate OTP
		int otp = 1000 + new Random().nextInt(9000);

		session.setAttribute("otp", otp);
		session.setAttribute("email", username);

		// 📧 Step 3: Send OTP
		EmailService.sendEmail("Your OTP is: " + otp, username, "Password Reset OTP");

		// ✅ Only if user exists → go to verify page
		return "verify";
	}

	@PostMapping("/verify")
	public String verify(@RequestParam("otp") String otp, HttpSession session) {

		Integer sessionOtp = (Integer) session.getAttribute("otp");

		if (sessionOtp == null) {
			// session expired
			return "redirect:/forgot?expired";
		}

		if (sessionOtp == Integer.parseInt(otp)) {
			return "cp";
		}

		return "verify";
	}

	@PostMapping("/cp")
	public String cp(@RequestParam("newPassword") String newPassword, @RequestParam("rePassword") String rePassword,
			HttpSession session) {

		String email = (String) session.getAttribute("email");

		if (email == null) {
			return "redirect:/forgot?expired";
		}

		// continue normal flow

		User user = UserRepository.getUserByUserName(email);

		if (!newPassword.equals(rePassword)) {
			return "cp";
		}

		user.setPassword(bcryptpasswordEncoder.encode(newPassword));
		UserRepository.save(user);

		// Clear session after success
		session.removeAttribute("otp");
		session.removeAttribute("email");

		return "login";
	}
}
