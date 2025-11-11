package edu.dosw.domain.model;

import edu.dosw.domain.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "customers")
public class Customer extends User {
    @Field("phone_number")
    private String phoneNumber;

    public Customer(CustomerBuilder builder) {
        super(builder.userId, builder.identityDocument, builder.email, builder.fullName);
        this.phoneNumber = builder.phoneNumber;
        setRole(Role.CUSTOMER);
        setPasswordHash(builder.passwordHash);
    }

    public static class CustomerBuilder {
        private String userId;
        private String identityDocument;
        private String email;
        private String fullName;
        private String passwordHash;
        private String phoneNumber;

        public CustomerBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public CustomerBuilder identityDocument(String identityDocument) {
            this.identityDocument = identityDocument;
            return this;
        }

        public CustomerBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CustomerBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public CustomerBuilder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public CustomerBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}
