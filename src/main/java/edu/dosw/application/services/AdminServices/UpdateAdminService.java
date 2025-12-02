package edu.dosw.application.services.AdminServices;

import edu.dosw.application.ports.AdminUseCases.UpdateAdminUseCase;
import edu.dosw.application.dto.command.AdminCommands.UpdateAdminCommand;
import edu.dosw.application.dto.AdminUpdateDTO;
import edu.dosw.domain.model.Admin;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.AdminRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.infrastructure.web.mappers.AdminWebMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateAdminService implements UpdateAdminUseCase {

    private final AdminRepositoryPort adminRepository;
    private final UserRepositoryPort userRepository;
    private final AdminWebMapper adminWebMapper;

    @Override
    public AdminUpdateDTO updateAdmin(UserId adminId, UpdateAdminCommand command) {
        Admin admin = adminRepository.findByUserId(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (command.email() != null && !command.email().equals(admin.getEmail().value())) {
            Email newEmail = new Email(command.email());
            if (userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        Admin updatedAdmin = new Admin(
                admin.getUserId(),
                command.identityDocument() != null ?
                        new IdentityDocument(command.identityDocument()) : admin.getIdentityDocument(),
                command.email() != null ?
                        new Email(command.email()) : admin.getEmail(),
                command.fullName() != null ?
                        new FullName(command.fullName()) : admin.getFullName(),
                admin.getPasswordHash()
        );

        Admin savedAdmin = adminRepository.save(updatedAdmin);
        userRepository.save(savedAdmin);

        return adminWebMapper.toUpdateDTO(savedAdmin);
    }
}