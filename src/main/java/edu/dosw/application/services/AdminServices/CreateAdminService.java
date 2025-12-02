package edu.dosw.application.services.AdminServices;

import edu.dosw.application.ports.AdminUseCases.CreateAdminUseCase;
import edu.dosw.application.dto.command.AdminCommands.CreateAdminCommand;
import edu.dosw.application.dto.AdminDTO;
import edu.dosw.domain.model.*;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.AdminRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
import edu.dosw.utils.IdGenerator;
import edu.dosw.infrastructure.web.mappers.AdminWebMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateAdminService implements CreateAdminUseCase {

    private final AdminRepositoryPort adminRepository;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final IdGenerator idGenerator;
    private final AdminWebMapper adminWebMapper;

    @Override
    public AdminDTO createAdmin(CreateAdminCommand command) {
        Email email = new Email(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Admin with this email already exists");
        }

        String encodedPassword = passwordEncoder.encode(command.password());

        Admin admin = new Admin(
                new UserId(idGenerator.generateUniqueId()),
                new IdentityDocument(command.identityDocument()),
                email,
                new FullName(command.fullName()),
                new PasswordHash(encodedPassword)
        );

        Admin savedAdmin = adminRepository.save(admin);
        userRepository.save(savedAdmin);

        return adminWebMapper.toDTO(savedAdmin);
    }
}