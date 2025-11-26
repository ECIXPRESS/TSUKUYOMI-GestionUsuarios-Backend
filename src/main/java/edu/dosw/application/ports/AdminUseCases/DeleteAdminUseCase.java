package edu.dosw.application.ports.AdminUseCases;


import edu.dosw.domain.model.ValueObject.UserId;

public interface DeleteAdminUseCase {
    void deleteAdmin(UserId adminId);
}