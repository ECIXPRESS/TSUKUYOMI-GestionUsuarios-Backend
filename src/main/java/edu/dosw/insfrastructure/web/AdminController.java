package edu.dosw.insfrastructure.web;

import edu.dosw.application.services.AdminService;
import edu.dosw.domain.model.Admin;
import edu.dosw.dto.AdminDTO;
import edu.dosw.dto.AdminUpdateDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/admins")
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody AdminDTO adminDTO) {
        Admin admin = adminService.createAdmin(adminDTO);
        return ResponseEntity.ok(admin);
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<Admin> getAdmin(@PathVariable String adminId) {
        Admin admin = adminService.getAdminById(adminId);
        return ResponseEntity.ok(admin);
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable String adminId, @RequestBody AdminUpdateDTO adminUpdateDTO) {
        Admin admin = adminService.updateAdmin(adminId, adminUpdateDTO);
        return ResponseEntity.ok(admin);
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String adminId) {
        adminService.deleteAdmin(adminId);
        return ResponseEntity.noContent().build();
    }
}