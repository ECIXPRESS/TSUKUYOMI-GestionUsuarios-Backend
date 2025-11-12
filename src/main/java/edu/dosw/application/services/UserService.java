package edu.dosw.application.services;

import edu.dosw.domain.model.User;
import edu.dosw.domain.ports.UserRepository;
import edu.dosw.dto.UserCredentialsDTO;
import edu.dosw.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCredentialsDTO getUserCredentialsByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserCredentialsDTO(
                user.getUserId(),
                user.getUserId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole()
        );
    }

    public UserCredentialsDTO getUserByEmailAndPassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResourceNotFoundException("Invalid password");
        }

        return new UserCredentialsDTO(
                user.getUserId(),
                user.getUserId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole()
        );
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteByUserId(userId);
    }
}