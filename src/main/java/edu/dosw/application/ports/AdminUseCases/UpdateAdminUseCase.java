package edu.dosw.application.ports.AdminUseCases;


import edu.dosw.application.dto.command.AdminCommands.UpdateAdminCommand;
import edu.dosw.application.dto.AdminUpdateDTO;
import edu.dosw.domain.model.ValueObject.UserId;

public interface UpdateAdminUseCase {
    AdminUpdateDTO updateAdmin(UserId adminId, UpdateAdminCommand command);
}