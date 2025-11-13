package edu.dosw.application.dto;

public record SellerUpdateDTO(
        String identityDocument,
        String email,
        String fullName,
        String companyName,
        String businessAddress
) {}