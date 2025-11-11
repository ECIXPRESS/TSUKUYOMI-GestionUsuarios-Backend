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
@Document(collection = "sellers")
public class Seller extends User {
    @Field("company_name")
    private String companyName;
    @Field("business_address")
    private String businessAddress;

    public Seller(SellerBuilder builder) {
        super(builder.userId, builder.identityDocument, builder.email, builder.fullName);
        this.companyName = builder.companyName;
        this.businessAddress = builder.businessAddress;
        setRole(Role.SELLER);
        setPasswordHash(builder.passwordHash);
    }

    public static class SellerBuilder {
        private String userId;
        private String identityDocument;
        private String email;
        private String fullName;
        private String passwordHash;
        private String companyName;
        private String businessAddress;

        public SellerBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public SellerBuilder identityDocument(String identityDocument) {
            this.identityDocument = identityDocument;
            return this;
        }

        public SellerBuilder email(String email) {
            this.email = email;
            return this;
        }

        public SellerBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public SellerBuilder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public SellerBuilder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public SellerBuilder businessAddress(String businessAddress) {
            this.businessAddress = businessAddress;
            return this;
        }

        public Seller build() {
            return new Seller(this);
        }
    }
}
