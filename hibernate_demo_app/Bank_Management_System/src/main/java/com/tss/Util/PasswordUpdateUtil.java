package com.tss.Util;

import com.tss.Entity.User;
import com.tss.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PasswordUpdateUtil {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Utility method to update all existing users' passwords to BCrypt encoding
     * This should be run once to migrate existing plain text passwords
     */
    public void updateAllPasswordsToBCrypt() {
        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            // Check if password is already BCrypt encoded
            if (!user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
                // Re-encode the existing password with BCrypt
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                userRepository.save(user);
                System.out.println("Updated password for user: " + user.getUsername());
            }
        }
    }
}

