package edu.dosw.application.services;

import edu.dosw.domain.model.Admin;
import edu.dosw.domain.ports.AdminRepository;
import edu.dosw.dto.AdminDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.utils.IdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdGenerator idGenerator;

    public Admin getAdminById(String adminId) {
        return adminRepository.findByUserId(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
    }

    public Admin createAdmin(AdminDTO request) {
        if (request.identityDocument() == null || request.email() == null || request.fullName() == null || request.password() == null) {
            throw new IllegalArgumentException("Admin data is incomplete");
        }
        if (adminRepository.existsByEmail(request.email())) {
            throw new BusinessException("Admin with this email already exists");
        }

        String userId = idGenerator.generateUniqueId();
        String passwordHash = passwordEncoder.encode(request.password());

        Admin admin = new Admin.AdminBuilder()
                .userId(userId)
                .identityDocument(request.identityDocument())
                .email(request.email())
                .fullName(request.fullName())
                .passwordHash(passwordHash)
                .build();

        return adminRepository.save(admin);
    }

    public void deleteAdmin(String adminId) {
        Admin admin = adminRepository.findByUserId(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        adminRepository.delete(admin);
    }
}