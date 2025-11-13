package edu.dosw.application.dto.command.SellerCommands;

public record UpdateSellerCommand(
        String identityDocument,
        String email,
        String fullName,
        String companyName,
        String businessAddress
) {}