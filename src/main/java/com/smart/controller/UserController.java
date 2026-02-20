package com.smart.controller;

import java.io.File;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Helper.Message;
import com.smart.dao.contactRepository;
import com.smart.dao.userRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private userRepository UserRepository;
	@Autowired
	private contactRepository contactRepository;

	@ModelAttribute
	private void commondata(Model model, Principal p) {

		if (p != null) {
			String username = p.getName();
			User user = UserRepository.getUserByUserName(username);
			model.addAttribute("user", user);
		}
	}

	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {

		String username = principal.getName();
		User user = this.UserRepository.getUserByUserName(username);

		int totalContacts = user.getContacts().size();

		// Example recent count (last 7 contacts)
		int recentCount = Math.min(totalContacts, 7);

		model.addAttribute("user", user);
		model.addAttribute("totalContacts", totalContacts);
		model.addAttribute("recentCount", recentCount);

		return "li/index";
	}

	@GetMapping("/addcontact")
	public String openAddContactForm(Model model) {
		model.addAttribute("contact", new Contact());
		return "li/addcontact";

	}

	@PostMapping("/addcontact")
	public String addContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
			Principal principal, @RequestParam("file") MultipartFile file, Model model,
			RedirectAttributes redirectAttributes) {

		// ❗ If validation fails
		if (result.hasErrors()) {
			model.addAttribute("message", new Message("Form has errors. Contact NOT saved!", "danger"));
			return "li/addcontact"; // stay on same page
		}

		try {

			// 🔹 Get logged in user
			String username = principal.getName();
			User user = this.UserRepository.getUserByUserName(username);

			// 🔹 Image Upload Logic
			if (file != null && !file.isEmpty()) {

				// Unique filename
				String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
				contact.setImage(fileName);

				// Path to src/main/resources/static/img
				String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/img/";

				File uploadPath = new File(uploadDir);

				// Create folder if not exists
				if (!uploadPath.exists()) {
					uploadPath.mkdirs();
				}

				// Save file
				File saveFile = new File(uploadPath, fileName);
				file.transferTo(saveFile);

			} else {
				// Default image
				contact.setImage("am.jpeg");
			}

			// 🔹 Set relationship
			contact.setUser(user);
			user.getContacts().add(contact);

			// 🔹 Save contact
			this.UserRepository.save(user);

			// ✅ Success message
			redirectAttributes.addFlashAttribute("message", new Message("Contact saved successfully!", "success"));

		} catch (Exception e) {
			e.printStackTrace();

			// ❌ Error message
			redirectAttributes.addFlashAttribute("message", new Message("Something went wrong!", "danger"));
		}

		return "redirect:/user/addcontact";
	}

//pagination
	// per page 5n
	// current page 0
	@GetMapping("/viewcontact/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal p) {

		String username = p.getName();
		User user = this.UserRepository.getUserByUserName(username);
		/*
		 * List<Contact> contacts = user.getContacts(); return "li/viewcontact";
		 */
		Pageable pageable = PageRequest.of(page, 3);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		System.out.println(contacts);
		return "li/viewcontact";
	}

	// showing particular contact details
	@GetMapping("/contact/{cId}")
	public String profile(@PathVariable("cId") Integer cId, Model m) {
		System.out.println(cId);

		Contact contact = this.contactRepository.findById(cId)
				.orElseThrow(() -> new RuntimeException("Contact not found"));
		System.out.println(contact);
		m.addAttribute("contact", contact);

		return "li/profile";
	}

	@GetMapping("/delete/{cid}")
	public String delete(@PathVariable("cid") Integer cId, Principal p) {

		Contact contact = this.contactRepository.findById(cId).get();

		// 🔹 Delete image from folder
		String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/img/";
		File uploadPath = new File(uploadDir);

		String image = contact.getImage();
		if (image != null && !image.equals("am.jpeg")) {
			File file = new File(uploadPath, image);
			if (file.exists()) {
				file.delete();
			}
		}

		User user = this.UserRepository.getUserByUserName(p.getName());
		user.getContacts().remove(contact);
		this.UserRepository.save(user);

		return "redirect:/user/viewcontact/0";
	}

// update controller
	@PostMapping("/update/{cid}")
	public String update(@PathVariable("cid") Integer cId, Model m) {
		Contact contact = this.contactRepository.findById(cId).get();
		m.addAttribute("contact", contact);
		return "li/update";
	}

	// update handler
	@PostMapping("/process-update")
	public String processupdate(@ModelAttribute Contact contact, Principal p, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		System.out.println("Contact " + contact);

		try {

			// 🔹 old contact details
			Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();

			// Path to image directory
			String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/img/";
			File uploadPath = new File(uploadDir);

			// 🔹 If new image uploaded
			if (file != null && !file.isEmpty()) {

				// 👉 DELETE OLD IMAGE (if not default)
				String oldImage = oldcontactDetail.getImage();
				if (oldImage != null && !oldImage.equals("am.jpeg")) {

					File oldFile = new File(uploadPath, oldImage);
					if (oldFile.exists()) {
						oldFile.delete();
					}
				}

				// 👉 Save new image
				String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
				contact.setImage(fileName);

				if (!uploadPath.exists()) {
					uploadPath.mkdirs();
				}

				File saveFile = new File(uploadPath, fileName);
				file.transferTo(saveFile);

			} else {
				// keep old image
				contact.setImage(oldcontactDetail.getImage());
			}

			User user = this.UserRepository.getUserByUserName(p.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);

		} catch (Exception e) {
			e.printStackTrace(); // log error
		}

		return "redirect:/user/viewcontact/0";
	}

	@GetMapping("/profile")
	public String userProfile(Model model, Principal principal) {

		String username = principal.getName();
		User user = this.UserRepository.getUserByUserName(username);

		model.addAttribute("user", user);
		return "li/uprofile";
	}
}
