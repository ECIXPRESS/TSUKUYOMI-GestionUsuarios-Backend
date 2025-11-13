package edu.dosw.application.services.SellerServices;

import edu.dosw.application.ports.SellerUseCases.UpdateSellerUseCase;
import edu.dosw.application.dto.command.SellerCommands.UpdateSellerCommand;
import edu.dosw.application.dto.SellerUpdateDTO;
import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.SellerRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.infrastructure.web.mappers.SellerWebMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateSellerService implements UpdateSellerUseCase {

    private final SellerRepositoryPort sellerRepository;
    private final UserRepositoryPort userRepository;
    private final SellerWebMapper sellerWebMapper;

    @Override
    public SellerUpdateDTO updateSeller(UserId sellerId, UpdateSellerCommand command) {
        Seller seller = sellerRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        if (command.email() != null && !command.email().equals(seller.getEmail().value())) {
            Email newEmail = new Email(command.email());
            if (userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        Seller updatedSeller = new Seller(
                seller.getUserId(),
                command.identityDocument() != null ?
                        new IdentityDocument(command.identityDocument()) : seller.getIdentityDocument(),
                command.email() != null ?
                        new Email(command.email()) : seller.getEmail(),
                command.fullName() != null ?
                        new FullName(command.fullName()) : seller.getFullName(),
                seller.getPasswordHash(),
                command.companyName() != null ? command.companyName() : seller.getCompanyName(),
                command.businessAddress() != null ? command.businessAddress() : seller.getBusinessAddress()
        );

        Seller savedSeller = sellerRepository.save(updatedSeller);
        userRepository.save(savedSeller);

        return sellerWebMapper.toUpdateDTO(savedSeller);
    }
}