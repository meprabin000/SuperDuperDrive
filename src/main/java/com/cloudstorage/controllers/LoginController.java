package com.cloudstorage.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String getLoginView(@ModelAttribute(name="signupSuccess") String signupSuccess, Model model) {
        model.addAttribute("signupSuccess", signupSuccess != null && !signupSuccess.isEmpty());
        return "login";
    }
}
