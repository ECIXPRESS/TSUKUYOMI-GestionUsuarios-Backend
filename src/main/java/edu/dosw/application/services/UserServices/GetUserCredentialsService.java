package edu.dosw.application.services.UserServices;

import edu.dosw.application.ports.UserUseCase.GetUserCredentialsUseCase;
import edu.dosw.application.dto.UserCredentialsDTO;
import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.Email;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.infrastructure.web.mappers.UserWebMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetUserCredentialsService implements GetUserCredentialsUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final UserWebMapper userWebMapper;

    @Override
    public UserCredentialsDTO getCredentialsByEmail(Email email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userWebMapper.toCredentialsDTO(user);
    }

    @Override
    public UserCredentialsDTO getUserByEmailAndPassword(Email email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash().value())) {
            throw new ResourceNotFoundException("Invalid password");
        }

        return userWebMapper.toCredentialsDTO(user);
    }
}