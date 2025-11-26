package edu.dosw.application.dto.command.AdminCommands;


public record CreateAdminCommand(
        String identityDocument,
        String email,
        String fullName,
        String password
) {}