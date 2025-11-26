package edu.dosw.application.dto.command.CustomerCommands;

public record UpdateCustomerCommand(
        String identityDocument,
        String email,
        String fullName,
        String phoneNumber
) {}