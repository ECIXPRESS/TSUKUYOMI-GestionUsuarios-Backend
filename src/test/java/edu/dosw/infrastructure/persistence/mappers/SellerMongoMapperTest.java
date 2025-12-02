package edu.dosw.infrastructure.persistence.mappers;

import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.model.enums.Role;
import edu.dosw.infrastructure.persistence.documents.SellerDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para SellerMongoMapper")
class SellerMongoMapperTest {

    private SellerMongoMapper mapper;
    private Seller seller;
    private SellerDocument sellerDocument;

    @BeforeEach
    void setUp() {
        mapper = new SellerMongoMapper();

        seller = new Seller(
                new UserId("seller-123"),
                new IdentityDocument("777888999"),
                new Email("seller@example.com"),
                new FullName("Seller User"),
                new PasswordHash("sellerHash123"),
                "Tech Company Inc.",
                "123 Business St, City"
        );

        sellerDocument = SellerDocument.builder()
                .userId("seller-123")
                .identityDocument("777888999")
                .email("seller@example.com")
                .fullName("Seller User")
                .passwordHash("sellerHash123")
                .role(Role.SELLER)
                .companyName("Tech Company Inc.")
                .businessAddress("123 Business St, City")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Debe convertir Seller a SellerDocument correctamente")
    void shouldConvertSellerToDocumentCorrectly() {
        // When
        SellerDocument result = mapper.toDocument(seller);

        // Then
        assertNotNull(result);
        assertEquals(seller.getUserId().value(), result.getUserId());
        assertEquals(seller.getIdentityDocument().value(), result.getIdentityDocument());
        assertEquals(seller.getEmail().value(), result.getEmail());
        assertEquals(seller.getFullName().value(), result.getFullName());
        assertEquals(seller.getPasswordHash().value(), result.getPasswordHash());
        assertEquals(Role.SELLER, result.getRole());
        assertEquals(seller.getCompanyName(), result.getCompanyName());
        assertEquals(seller.getBusinessAddress(), result.getBusinessAddress());
        assertEquals(seller.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    @DisplayName("Debe convertir SellerDocument a Seller correctamente")
    void shouldConvertDocumentToSellerCorrectly() {
        // When
        Seller result = mapper.toDomain(sellerDocument);

        // Then
        assertNotNull(result);
        assertEquals(sellerDocument.getUserId(), result.getUserId().value());
        assertEquals(sellerDocument.getIdentityDocument(), result.getIdentityDocument().value());
        assertEquals(sellerDocument.getEmail(), result.getEmail().value());
        assertEquals(sellerDocument.getFullName(), result.getFullName().value());
        assertEquals(sellerDocument.getPasswordHash(), result.getPasswordHash().value());
        assertEquals(Role.SELLER, result.getRole());
        assertEquals(sellerDocument.getCompanyName(), result.getCompanyName());
        assertEquals(sellerDocument.getBusinessAddress(), result.getBusinessAddress());
    }

    @Test
    @DisplayName("Debe retornar null cuando el documento es null")
    void shouldReturnNullWhenDocumentIsNull() {
        // When
        Seller result = mapper.toDomain(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Debe preservar el nombre de la compañía correctamente")
    void shouldPreserveCompanyNameCorrectly() {
        // Given
        Seller sellerWithDifferentCompany = new Seller(
                new UserId("seller-456"),
                new IdentityDocument("444555666"),
                new Email("another.seller@example.com"),
                new FullName("Another Seller"),
                new PasswordHash("anotherSellerHash"),
                "Global Trade Co.",
                "456 Commerce Ave"
        );

        // When
        SellerDocument result = mapper.toDocument(sellerWithDifferentCompany);

        // Then
        assertEquals("Global Trade Co.", result.getCompanyName());
    }

    @Test
    @DisplayName("Debe preservar la dirección de negocio correctamente")
    void shouldPreserveBusinessAddressCorrectly() {
        // Given
        Seller sellerWithLongAddress = new Seller(
                new UserId("seller-789"),
                new IdentityDocument("999000111"),
                new Email("long.address@example.com"),
                new FullName("Long Address Seller"),
                new PasswordHash("longAddressHash"),
                "Enterprise Solutions LLC",
                "789 Industrial Park, Building 5, Suite 300, Metropolitan Area"
        );

        // When
        SellerDocument result = mapper.toDocument(sellerWithLongAddress);

        // Then
        assertEquals("789 Industrial Park, Building 5, Suite 300, Metropolitan Area", result.getBusinessAddress());
    }

    @Test
    @DisplayName("Debe asignar role SELLER automáticamente")
    void shouldAssignSellerRoleAutomatically() {
        // When
        SellerDocument result = mapper.toDocument(seller);

        // Then
        assertEquals(Role.SELLER, result.getRole());
    }

    @Test
    @DisplayName("Debe manejar conversión bidireccional correctamente")
    void shouldHandleBidirectionalConversionCorrectly() {
        // When
        SellerDocument document = mapper.toDocument(seller);
        Seller reconstructedSeller = mapper.toDomain(document);

        // Then
        assertEquals(seller.getUserId().value(), reconstructedSeller.getUserId().value());
        assertEquals(seller.getEmail().value(), reconstructedSeller.getEmail().value());
        assertEquals(seller.getCompanyName(), reconstructedSeller.getCompanyName());
        assertEquals(seller.getBusinessAddress(), reconstructedSeller.getBusinessAddress());
    }

    @Test
    @DisplayName("Debe preservar la fecha de creación")
    void shouldPreserveCreatedAt() {
        // Given
        LocalDateTime specificDate = LocalDateTime.of(2024, 4, 15, 12, 0);
        SellerDocument documentWithDate = SellerDocument.builder()
                .userId("seller-999")
                .identityDocument("111222333")
                .email("dated.seller@example.com")
                .fullName("Dated Seller")
                .passwordHash("datedSellerHash")
                .role(Role.SELLER)
                .companyName("Dated Company")
                .businessAddress("Dated Address")
                .createdAt(specificDate)
                .build();

        // When
        Seller result = mapper.toDomain(documentWithDate);
        SellerDocument backToDocument = mapper.toDocument(result);

        // Then
        assertEquals(result.getCreatedAt(), backToDocument.getCreatedAt());
    }
}
