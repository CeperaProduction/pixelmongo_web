package ru.pixelmongo.pixelmongo.controllers;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.pixelmongo.pixelmongo.data.User;
import ru.pixelmongo.pixelmongo.repositories.UserRepository;

@Controller
@RequestMapping("/")
public class IndexController {
	
	@Autowired
	private UserRepository users;

	@GetMapping
	public String index(Model model) {
		model.addAttribute("users", users.findAll());
		return "index";
	}
	
	@GetMapping("/testuser")
	public String addTestUser() {
		String name = randomString(10);
		String email = randomString(10)+'@'+randomString(6)+".com";
		String password = randomString(20);
		
		users.save(new User(name, email, password));
		return "redirect:/";
	}
	
	private String randomString(int length) {
		Random r = new Random();
		String str = "";
		for(int i = 0; i < length; i++) {
			str += (char)('a'+r.nextInt('z'-'a'));
		}
		return str;
	}
	
}
