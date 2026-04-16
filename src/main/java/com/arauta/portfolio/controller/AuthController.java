package com.arauta.portfolio.controller;

import com.arauta.portfolio.dto.ChangePasswordForm;
import com.arauta.portfolio.dto.RegisterForm;
import com.arauta.portfolio.service.UserService;

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

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "public/change-password";
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

    @PostMapping("/change-password")
    public String changePasswordSubmit(
            @Valid @ModelAttribute("form") ChangePasswordForm form,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails currentUser,
            Model model) {

        if (bindingResult.hasErrors()) return "public/change-password";

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("confirmMismatch", true);
            return "/change-password";
        }

        try {
            userService.changePassword(currentUser.getUsername(), form.getOldPassword(), form.getNewPassword());
        } catch (UserService.WrongPasswordException ex) {
            model.addAttribute("wrongPassword", true);
            return "/change-password";
        }

        return "redirect:/change-password?success";
    }
}
