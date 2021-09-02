package ru.pixelmongo.pixelmongo.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
	
	@Value("${test.test}")
	private String test;

	@GetMapping
	public String index(Model model) {
		model.addAttribute("test", test);
		return "index";
	}
	
}
