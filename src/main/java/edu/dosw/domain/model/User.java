package edu.dosw.domain.model;

import edu.dosw.domain.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    protected String userId;
    protected String identityDocument;
    protected String email;
    protected String fullName;
    protected String passwordHash;
    protected Role role;
    protected LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String userId, String identityDocument, String email, String fullName) {
        this.userId = userId;
        this.identityDocument = identityDocument;
        this.email = email;
        this.fullName = fullName;
        this.createdAt = LocalDateTime.now();
    }
}
