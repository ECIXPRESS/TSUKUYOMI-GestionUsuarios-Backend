package edu.dosw.application.dto.command.AdminCommands;

public record UpdateAdminCommand(
        String identityDocument,
        String email,
        String fullName
) {}