package edu.dosw.infrastructure.persistence.mappers;

import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.infrastructure.persistence.documents.UserDocument;
import org.springframework.stereotype.Component;

@Component
public class UserMongoMapper {

    public UserDocument toDocument(User user) {
        return UserDocument.builder()
                .userId(user.getUserId().value())
                .identityDocument(user.getIdentityDocument().value())
                .email(user.getEmail().value())
                .fullName(user.getFullName().value())
                .passwordHash(user.getPasswordHash().value())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toDomain(UserDocument document) {
        if (document == null) return null;

        return new User(
                new UserId(document.getUserId()),
                new IdentityDocument(document.getIdentityDocument()),
                new Email(document.getEmail()),
                new FullName(document.getFullName()),
                new PasswordHash(document.getPasswordHash()),
                document.getRole()
        );
    }
}