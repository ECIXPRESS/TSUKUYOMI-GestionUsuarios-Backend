package edu.dosw.infrastructure.persistence.documents;

import edu.dosw.domain.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class CustomerDocument {
    @Id
    private String userId;
    private String identityDocument;
    private String email;
    private String fullName;
    private String passwordHash;
    private Role role;

    @Field("phone_number")
    private String phoneNumber;

    private LocalDateTime createdAt;
}