package edu.dosw.infrastructure.web;

import edu.dosw.application.ports.UserUseCase.GetUserCredentialsUseCase;
import edu.dosw.domain.model.ValueObject.Email;
import edu.dosw.application.dto.UserCredentialsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final GetUserCredentialsUseCase getUserCredentialsUseCase;

    @GetMapping("/credentials/{email}")
    public ResponseEntity<UserCredentialsDTO> getUserCredentials(@PathVariable String email) {
        UserCredentialsDTO credentials = getUserCredentialsUseCase.getCredentialsByEmail(new Email(email));
        return ResponseEntity.ok(credentials);
    }

    @GetMapping("/credentials/auth")
    public ResponseEntity<UserCredentialsDTO> authenticateUser(@RequestParam String email,
                                                               @RequestParam String password) {
        UserCredentialsDTO credentials = getUserCredentialsUseCase.getUserByEmailAndPassword(new Email(email), password);
        return ResponseEntity.ok(credentials);
    }
}