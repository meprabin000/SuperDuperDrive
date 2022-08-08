package com.cloudstorage.services;

import com.cloudstorage.forms.CredentialForm;
import com.cloudstorage.mappers.CredentialMapper;
import com.cloudstorage.models.Credential;
import com.cloudstorage.models.User;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class CredentialService {
    private CredentialMapper credentialMapper;
    private EncryptionService encryptionService;

    public CredentialService(CredentialMapper credentialMapper, EncryptionService encryptionService, UserService userService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    public List<Credential> getCredentialsFor(Integer userId) {
        return credentialMapper.getCredentialsFor(userId);
    }

    public Credential getCrediantialOf(Integer credentialId) {
        Credential credential = credentialMapper.getCredentialOf(credentialId);
        String encodedKey = credential.getKey();
        String password = encryptionService.decryptValue(credential.getPassword(), encodedKey);
        credential.setPassword(password);
        return credential;
    }

    public Integer createCredential(CredentialForm credentialForm, Integer userId) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptedPassword = encryptionService.encryptValue(credentialForm.getPassword(), encodedKey);
        return credentialMapper.insert(new Credential(null, credentialForm.getUrl(), credentialForm.getUsername(), encodedKey, encryptedPassword, userId));
    }

    public void deleteCredential(Integer credentialId) {
        credentialMapper.delete(credentialId);
    }

    public void updateCredential(CredentialForm credentialForm) {
        Credential credential = getCrediantialOf(credentialForm.getCredentialId());
        String encodedKey = credential.getKey();
        String password = encryptionService.encryptValue(credential.getPassword(), encodedKey);
        credential.setUrl(credentialForm.getUrl());
        credential.setUsername(credentialForm.getUsername());
        credential.setPassword(password);
        credentialMapper.update(credential);
    }
}
