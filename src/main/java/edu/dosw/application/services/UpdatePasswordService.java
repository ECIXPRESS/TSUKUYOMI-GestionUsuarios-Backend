package edu.dosw.application.services;

import edu.dosw.application.ports.UpdatePasswordUseCase;
import edu.dosw.application.dto.command.UpdatePasswordCommand;
import edu.dosw.application.dto.PasswordUpdateRequestDTO;
import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.PasswordHash;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
import edu.dosw.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdatePasswordService implements UpdatePasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    public PasswordUpdateRequestDTO updatePassword(UpdatePasswordCommand command) {
        User user = userRepository.findByUserId(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String encodedPassword = passwordEncoder.encode(command.newPassword());
        user.changePassword(new PasswordHash(encodedPassword));

        userRepository.save(user);
        return new PasswordUpdateRequestDTO(command.newPassword());
    }
}