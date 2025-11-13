package edu.dosw.application.services.AdminServices;

import edu.dosw.application.ports.AdminUseCases.GetAdminUseCase;
import edu.dosw.application.dto.AdminDTO;
import edu.dosw.domain.model.Admin;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.ports.AdminRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.infrastructure.web.mappers.AdminWebMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAdminService implements GetAdminUseCase {

    private final AdminRepositoryPort adminRepository;
    private final AdminWebMapper adminWebMapper;

    @Override
    public AdminDTO getAdminById(UserId adminId) {
        Admin admin = adminRepository.findByUserId(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        return adminWebMapper.toDTO(admin);
    }
}