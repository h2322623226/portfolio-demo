package com.example.securitydemo.controller;

import com.example.securitydemo.dto.RegisterForm;
import com.example.securitydemo.service.UserService;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(
            @Valid @ModelAttribute("form") RegisterForm form,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) return "register";

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("passwordMismatch", true);
            return "register";
        }

        try {
            userService.register(form.getUsername(), form.getPassword());
        } catch (UserService.UsernameAlreadyExistsException ex) {
            model.addAttribute("usernameExists", true);
            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "public/change-password";
    }

    @PostMapping("/change-password")
    public String changePasswordSubmit(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            @AuthenticationPrincipal UserDetails currentUser,
            Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("confirmMismatch", true);
            return "public/change-password";
        }

        try {
            userService.changePassword(currentUser.getUsername(), oldPassword, newPassword);
        } catch (UserService.WrongPasswordException ex) {
            model.addAttribute("wrongPassword", true);
            return "public/change-password";
        }

        return "redirect:/change-password?success";
    }
}
