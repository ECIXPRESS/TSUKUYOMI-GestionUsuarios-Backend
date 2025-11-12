package edu.dosw.insfrastructure.web;

import edu.dosw.application.services.UserService;
import edu.dosw.dto.UserCredentialsDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/credentials/{email}")
    public ResponseEntity<UserCredentialsDTO> getUserCredentialsByEmail(@PathVariable String email) {
        UserCredentialsDTO credentials = userService.getUserCredentialsByEmail(email);
        return ResponseEntity.ok(credentials);
    }

    @GetMapping("/credentials/{email}/{password}")
    public ResponseEntity<UserCredentialsDTO> getUserByEmailAndPassword(
            @PathVariable String email,
            @PathVariable String password) {
        UserCredentialsDTO credentials = userService.getUserByEmailAndPassword(email, password);
        return ResponseEntity.ok(credentials);
    }
}