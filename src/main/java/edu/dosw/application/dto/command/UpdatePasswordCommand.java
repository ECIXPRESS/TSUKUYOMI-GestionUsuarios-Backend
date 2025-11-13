package edu.dosw.application.dto.command;

import edu.dosw.domain.model.ValueObject.UserId;

public record UpdatePasswordCommand(
        UserId userId,
        String newPassword
) {}