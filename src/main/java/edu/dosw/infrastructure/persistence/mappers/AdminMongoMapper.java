package edu.dosw.infrastructure.persistence.mappers;


import edu.dosw.domain.model.Admin;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.infrastructure.persistence.documents.AdminDocument;
import org.springframework.stereotype.Component;

@Component
public class AdminMongoMapper {

    public AdminDocument toDocument(Admin admin) {
        return AdminDocument.builder()
                .userId(admin.getUserId().value())
                .identityDocument(admin.getIdentityDocument().value())
                .email(admin.getEmail().value())
                .fullName(admin.getFullName().value())
                .passwordHash(admin.getPasswordHash().value())
                .role(admin.getRole())
                .createdAt(admin.getCreatedAt())
                .build();
    }

    public Admin toDomain(AdminDocument document) {
        if (document == null) return null;

        return new Admin(
                new UserId(document.getUserId()),
                new IdentityDocument(document.getIdentityDocument()),
                new Email(document.getEmail()),
                new FullName(document.getFullName()),
                new PasswordHash(document.getPasswordHash())
        );
    }
}