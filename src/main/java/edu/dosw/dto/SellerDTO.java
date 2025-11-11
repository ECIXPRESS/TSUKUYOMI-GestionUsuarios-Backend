package edu.dosw.dto;

public record SellerDTO(
        String email,
        String fullName,
        String password,
        String identityDocument,
        String companyName,
        String businessAddress
) {}