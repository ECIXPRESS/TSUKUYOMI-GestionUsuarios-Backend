package edu.dosw.infrastructure.web;

import edu.dosw.application.dto.*;
import edu.dosw.application.services.PasswordResetService;
import edu.dosw.domain.model.ValueObject.Email;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/password/reset-request")
    public ResponseEntity<VerificationResponse> requestPasswordReset(@RequestBody @Valid PasswordResetRequest request) {
        Email email = new Email(request.email());
        passwordResetService.requestPasswordReset(email);

        // Siempre retornar éxito para no revelar si el email existe
        return ResponseEntity.ok(new VerificationResponse(true, "If the email exists, a verification code has been sent"));
    }

    @PostMapping("/password/verify-code")
    public ResponseEntity<VerificationResponse> verifyResetCode(@RequestBody @Valid VerifyCodeRequest request) {
        Email email = new Email(request.email());
        passwordResetService.verifyCode(email, request.code());

        return ResponseEntity.ok(new VerificationResponse(true, "Code verified successfully"));
    }

    @PutMapping("/password/reset")
    public ResponseEntity<VerificationResponse> resetPassword(@RequestBody @Valid PasswordResetConfirmRequest request) {
        Email email = new Email(request.email());
        passwordResetService.resetPassword(email, request.code(), request.newPassword());

        return ResponseEntity.ok(new VerificationResponse(true, "Password reset successfully"));
    }
}