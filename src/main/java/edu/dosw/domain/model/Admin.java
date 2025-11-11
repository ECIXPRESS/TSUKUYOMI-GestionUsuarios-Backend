package edu.dosw.domain.model;

import edu.dosw.domain.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "administrators")
public class Admin extends User {

    public Admin(AdminBuilder builder) {
        super(builder.userId, builder.identityDocument, builder.email, builder.fullName);
        setRole(Role.ADMIN);
        setPasswordHash(builder.passwordHash);
    }

    public static class AdminBuilder {
        private String userId;
        private String identityDocument;
        private String email;
        private String fullName;
        private String passwordHash;

        public AdminBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public AdminBuilder identityDocument(String identityDocument) {
            this.identityDocument = identityDocument;
            return this;
        }

        public AdminBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AdminBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public AdminBuilder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Admin build() {
            return new Admin(this);
        }
    }
}
