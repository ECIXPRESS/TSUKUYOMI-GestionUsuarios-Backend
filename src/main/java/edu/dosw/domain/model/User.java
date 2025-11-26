package edu.dosw.domain.model;

import edu.dosw.domain.model.enums.Role;
import edu.dosw.domain.model.ValueObject.*;
import java.time.LocalDateTime;

public class User {
    private final UserId userId;
    private final IdentityDocument identityDocument;
    private final Email email;
    private final FullName fullName;
    private PasswordHash passwordHash;
    private final Role role;
    private final LocalDateTime createdAt;

    public User(UserId userId, IdentityDocument identityDocument, Email email,
                FullName fullName, PasswordHash passwordHash, Role role) {
        this.userId = userId;
        this.identityDocument = identityDocument;
        this.email = email;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public void changePassword(PasswordHash newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public UserId getUserId() { return userId; }
    public IdentityDocument getIdentityDocument() { return identityDocument; }
    public Email getEmail() { return email; }
    public FullName getFullName() { return fullName; }
    public PasswordHash getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}