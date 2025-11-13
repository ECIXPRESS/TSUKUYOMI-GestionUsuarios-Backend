package edu.dosw.infrastructure.web.mappers;


import edu.dosw.application.dto.command.AdminCommands.CreateAdminCommand;
import edu.dosw.application.dto.command.AdminCommands.UpdateAdminCommand;
import edu.dosw.domain.model.Admin;
import edu.dosw.application.dto.AdminDTO;
import edu.dosw.application.dto.AdminUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class AdminWebMapper {

    public CreateAdminCommand toCommand(AdminDTO dto) {
        return new CreateAdminCommand(
                dto.identityDocument(),
                dto.email(),
                dto.fullName(),
                dto.password()
        );
    }

    public UpdateAdminCommand toCommand(AdminUpdateDTO dto) {
        return new UpdateAdminCommand(
                dto.identityDocument(),
                dto.email(),
                dto.fullName()
        );
    }

    public AdminDTO toDTO(Admin admin) {
        return new AdminDTO(
                admin.getEmail().value(),
                admin.getFullName().value(),
                "",
                admin.getIdentityDocument().value()
        );
    }

    public AdminUpdateDTO toUpdateDTO(Admin admin) {
        return new AdminUpdateDTO(
                admin.getIdentityDocument().value(),
                admin.getEmail().value(),
                admin.getFullName().value()
        );
    }
}