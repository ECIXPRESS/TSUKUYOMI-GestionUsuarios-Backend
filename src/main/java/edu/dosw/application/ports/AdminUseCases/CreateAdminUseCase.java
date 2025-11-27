package edu.dosw.application.ports.AdminUseCases;


import edu.dosw.application.dto.command.AdminCommands.CreateAdminCommand;
import edu.dosw.application.dto.AdminDTO;

public interface CreateAdminUseCase {
    AdminDTO createAdmin(CreateAdminCommand command);
}
