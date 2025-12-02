package edu.dosw.infrastructure.persistence.documents;

import edu.dosw.domain.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserDocument {
    @Id
    private String userId;
    private String identityDocument;
    private String email;
    private String fullName;
    private String passwordHash;
    private Role role;
    private LocalDateTime createdAt;
}