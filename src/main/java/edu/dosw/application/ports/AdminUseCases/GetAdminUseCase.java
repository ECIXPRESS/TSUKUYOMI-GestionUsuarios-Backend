package edu.dosw.application.ports.AdminUseCases;


import edu.dosw.application.dto.AdminDTO;
import edu.dosw.domain.model.ValueObject.UserId;

public interface GetAdminUseCase {
    AdminDTO getAdminById(UserId adminId);
}