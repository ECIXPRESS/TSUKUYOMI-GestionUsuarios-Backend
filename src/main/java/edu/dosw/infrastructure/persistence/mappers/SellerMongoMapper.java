package edu.dosw.infrastructure.persistence.mappers;


import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.infrastructure.persistence.documents.SellerDocument;
import org.springframework.stereotype.Component;

@Component
public class SellerMongoMapper {

    public SellerDocument toDocument(Seller seller) {
        return SellerDocument.builder()
                .userId(seller.getUserId().value())
                .identityDocument(seller.getIdentityDocument().value())
                .email(seller.getEmail().value())
                .fullName(seller.getFullName().value())
                .passwordHash(seller.getPasswordHash().value())
                .role(seller.getRole())
                .companyName(seller.getCompanyName())
                .businessAddress(seller.getBusinessAddress())
                .createdAt(seller.getCreatedAt())
                .build();
    }

    public Seller toDomain(SellerDocument document) {
        if (document == null) return null;

        return new Seller(
                new UserId(document.getUserId()),
                new IdentityDocument(document.getIdentityDocument()),
                new Email(document.getEmail()),
                new FullName(document.getFullName()),
                new PasswordHash(document.getPasswordHash()),
                document.getCompanyName(),
                document.getBusinessAddress()
        );
    }
}