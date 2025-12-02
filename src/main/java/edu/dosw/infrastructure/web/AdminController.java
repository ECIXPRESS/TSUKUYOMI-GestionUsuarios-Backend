package edu.dosw.infrastructure.web;

import edu.dosw.application.ports.AdminUseCases.*;
import edu.dosw.application.dto.command.AdminCommands.CreateAdminCommand;
import edu.dosw.application.dto.command.AdminCommands.UpdateAdminCommand;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.application.dto.AdminDTO;
import edu.dosw.application.dto.AdminUpdateDTO;
import edu.dosw.infrastructure.web.mappers.AdminWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/admins")
@RequiredArgsConstructor
public class AdminController {

    private final CreateAdminUseCase createAdminUseCase;
    private final GetAdminUseCase getAdminUseCase;
    private final UpdateAdminUseCase updateAdminUseCase;
    private final DeleteAdminUseCase deleteAdminUseCase;
    private final AdminWebMapper adminWebMapper;

    @PostMapping
    public ResponseEntity<AdminDTO> createAdmin(@RequestBody AdminDTO adminDTO) {
        CreateAdminCommand command = adminWebMapper.toCommand(adminDTO);
        AdminDTO result = createAdminUseCase.createAdmin(command);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<AdminDTO> getAdmin(@PathVariable String adminId) {
        AdminDTO admin = getAdminUseCase.getAdminById(new UserId(adminId));
        return ResponseEntity.ok(admin);
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<AdminUpdateDTO> updateAdmin(@PathVariable String adminId,
                                                      @RequestBody AdminUpdateDTO adminUpdateDTO) {
        UpdateAdminCommand command = adminWebMapper.toCommand(adminUpdateDTO);
        AdminUpdateDTO result = updateAdminUseCase.updateAdmin(new UserId(adminId), command);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String adminId) {
        deleteAdminUseCase.deleteAdmin(new UserId(adminId));
        return ResponseEntity.noContent().build();
    }
}