package edu.dosw.domain.model;

import edu.dosw.domain.model.enums.Role;
import edu.dosw.domain.model.ValueObject.*;

public class Admin extends User {

    public Admin(UserId userId, IdentityDocument identityDocument, Email email,
                 FullName fullName, PasswordHash passwordHash) {
        super(userId, identityDocument, email, fullName, passwordHash, Role.ADMIN);
    }
}