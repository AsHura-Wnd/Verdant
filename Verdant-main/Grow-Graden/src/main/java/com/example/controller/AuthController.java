package com.example.controller;

import com.example.dto.RegisterDto;
import com.example.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please verify your credentials.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out of your botanical session.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new RegisterDto());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registerDto") RegisterDto registerDto,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerUser(registerDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Welcome to Verdant, " + registerDto.getFullName() + "! Your botanical account is active. Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }
    }
}
