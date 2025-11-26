package edu.dosw.application.dto.command.SellerCommands;


public record CreateSellerCommand(
        String identityDocument,
        String email,
        String fullName,
        String password,
        String companyName,
        String businessAddress
) {}