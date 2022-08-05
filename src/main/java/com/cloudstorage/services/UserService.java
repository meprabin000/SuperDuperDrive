package com.cloudstorage.services;

import com.cloudstorage.mappers.UserMapper;
import com.cloudstorage.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class UserService {
    private UserMapper userMapper;
    private HashService hashService;

    public UserService(UserMapper userMapper, HashService hashService) {
        this.userMapper = userMapper;
        this.hashService = hashService;
    }

    public User getUser(String username) {
        return userMapper.getUser(username);
    }

    public Integer createUser(User user) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String hashedPassword = hashService.getHashedValue(user.getPassword(), encodedSalt);
        return userMapper.insert(new User(null, user.getUsername(), encodedSalt, hashedPassword, user.getFirstName(), user.getLastName()));
    }

    public List<User> getUsers() {
        return userMapper.getUsers();
    }
}
