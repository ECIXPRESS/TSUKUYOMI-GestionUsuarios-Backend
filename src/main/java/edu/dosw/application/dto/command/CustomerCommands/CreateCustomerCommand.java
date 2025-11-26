package edu.dosw.application.dto.command.CustomerCommands;

public record CreateCustomerCommand(
        String identityDocument,
        String email,
        String fullName,
        String password,
        String phoneNumber
) {}