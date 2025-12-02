package edu.dosw.infrastructure.persistence.mappers;


import edu.dosw.domain.model.Customer;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.infrastructure.persistence.documents.CustomerDocument;
import org.springframework.stereotype.Component;

@Component
public class CustomerMongoMapper {

    public CustomerDocument toDocument(Customer customer) {
        return CustomerDocument.builder()
                .userId(customer.getUserId().value())
                .identityDocument(customer.getIdentityDocument().value())
                .email(customer.getEmail().value())
                .fullName(customer.getFullName().value())
                .passwordHash(customer.getPasswordHash().value())
                .role(customer.getRole())
                .phoneNumber(customer.getPhoneNumber())
                .createdAt(customer.getCreatedAt())
                .build();
    }

    public Customer toDomain(CustomerDocument document) {
        if (document == null) return null;

        return new Customer(
                new UserId(document.getUserId()),
                new IdentityDocument(document.getIdentityDocument()),
                new Email(document.getEmail()),
                new FullName(document.getFullName()),
                new PasswordHash(document.getPasswordHash()),
                document.getPhoneNumber()
        );
    }
}