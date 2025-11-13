package edu.dosw.domain.model;

import edu.dosw.domain.model.enums.Role;
import edu.dosw.domain.model.ValueObject.*;
import java.time.LocalDateTime;

public class Customer extends User {
    private final String phoneNumber;

    public Customer(UserId userId, IdentityDocument identityDocument, Email email,
                    FullName fullName, PasswordHash passwordHash, String phoneNumber) {
        super(userId, identityDocument, email, fullName, passwordHash, Role.CUSTOMER);
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() { return phoneNumber; }
}