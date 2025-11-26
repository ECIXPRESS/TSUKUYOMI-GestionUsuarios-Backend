package edu.dosw.application.services.AdminServices;

import edu.dosw.application.ports.AdminUseCases.DeleteAdminUseCase;
import edu.dosw.domain.model.Admin;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.ports.AdminRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteAdminService implements DeleteAdminUseCase {

    private final AdminRepositoryPort adminRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public void deleteAdmin(UserId adminId) {
        Admin admin = adminRepository.findByUserId(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        adminRepository.delete(admin);
        userRepository.deleteByUserId(adminId);
    }
}