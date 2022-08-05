package com.cloudstorage.controllers;

import com.cloudstorage.forms.ButtonForm;
import com.cloudstorage.forms.CredentialForm;
import com.cloudstorage.models.Credential;
import com.cloudstorage.models.User;
import com.cloudstorage.services.AuthenticationService;
import com.cloudstorage.services.HomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Controller
public class HomeController {
    private final Logger logger;
    private HomeService homeService;
    

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
        this.logger = LoggerFactory.getLogger(HomeController.class);
    }


    @GetMapping(value = {"/home", "/credential", "/deleteCredential", "/updateCredential"})
    public String homeView(CredentialForm credentialForm, ButtonForm buttonForm, Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("credentials", homeService.getCredentialsFor(currentUsername));
        return "home";
    }

    @PostMapping("/credential")
    public String addCredential(CredentialForm credentialForm, ButtonForm buttonForm, Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String errorMessage = homeService.addCredential(credentialForm, currentUsername);
        if(errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            return "error";
        }
        model.addAttribute("credentials", homeService.getCredentialsFor(currentUsername));
        return "home";
    }

    @PostMapping("/deleteCredential")
    public String deleteCredential(CredentialForm credentialForm, ButtonForm buttonForm, Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        homeService.deleteCredential(currentUsername, buttonForm.getButtonId());
        model.addAttribute("credentials", homeService.getCredentialsFor(currentUsername));
        return "home";
    }

    @PostMapping("/updateCredential")
    public String updateCredential(CredentialForm credentialForm, ButtonForm buttonForm, Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        homeService.updateCredential(currentUsername,credentialForm);
        model.addAttribute("credentials", homeService.getCredentialsFor(currentUsername));
        return "home";
    }

    @GetMapping("getCredential")
    @ResponseBody
    public Optional<Credential> getCredential(Integer credentialId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.of(homeService.getCredentialOf(currentUsername, credentialId));
    }

    @PostMapping("/deleteACredential")
    public void deleteACredential(Integer Id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        homeService.deleteCredential(currentUsername, Id);
    }
}
