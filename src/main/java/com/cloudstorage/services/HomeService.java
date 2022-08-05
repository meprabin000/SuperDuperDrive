package com.cloudstorage.services;

import com.cloudstorage.forms.CredentialForm;
import com.cloudstorage.models.Credential;
import com.cloudstorage.models.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
public class HomeService {
    private UserService userService;
    private CredentialService credentialService;

    public HomeService(UserService userService, CredentialService credentialService) {
        this.userService = userService;
        this.credentialService = credentialService;
    }

    public String addCredential(CredentialForm credentialForm, String username) {
        Integer userId = userService.getUser(username).getUserId();
        credentialService.createCredential(credentialForm, userId);
        return null;
    }

    public List<Credential> getCredentialsFor(String username) {
        Integer userId = userService.getUser(username).getUserId();
        return credentialService.getCredentialsFor(userId);
    }

    public void deleteCredential(String username, Integer id) {
        Integer userId = userService.getUser(username).getUserId();
        credentialService.deleteCredential(userId, id);
    }

    public Credential getCredentialOf(String username, Integer credentialId) {
        Integer userId = userService.getUser(username).getUserId();
        return credentialService.getCrediantialOf(userId, credentialId);
    }

    public void updateCredential(String username, CredentialForm credentialForm) {
        Integer userId = userService.getUser(username).getUserId();
        credentialService.updateCredential(userId, credentialForm);
    }
}
