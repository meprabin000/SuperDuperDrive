package com.cloudstorage.controllers;

import com.cloudstorage.models.User;
import com.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/signup")
public class SignupController {

    private UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String signupView() {
        return "signup";
    }

    @PostMapping()
    public String signup(@ModelAttribute User user, Model model, final RedirectAttributes redirectAttributes) {

        // user already exists
        if(userService.getUser(user.getUsername()) != null) {
            model.addAttribute("signupError", "Username already exists. Please try different username");
        }
        else {
            int rows_added = userService.createUser(user);
            if(rows_added > 0) {
                redirectAttributes.addFlashAttribute("signupSuccess", "true");
                return "redirect:/login";
            }
            else {
                model.addAttribute("signupError", "There was an error signing up. Please try again.");
            }

        }

        return "signup";
    }
}
