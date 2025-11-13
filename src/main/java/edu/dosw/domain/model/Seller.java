package edu.dosw.domain.model;

import edu.dosw.domain.model.enums.Role;
import edu.dosw.domain.model.ValueObject.*;

public class Seller extends User {
    private final String companyName;
    private final String businessAddress;

    public Seller(UserId userId, IdentityDocument identityDocument, Email email,
                  FullName fullName, PasswordHash passwordHash,
                  String companyName, String businessAddress) {
        super(userId, identityDocument, email, fullName, passwordHash, Role.SELLER);
        this.companyName = companyName;
        this.businessAddress = businessAddress;
    }

    public String getCompanyName() { return companyName; }
    public String getBusinessAddress() { return businessAddress; }
}