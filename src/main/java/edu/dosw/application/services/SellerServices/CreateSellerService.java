package edu.dosw.application.services.SellerServices;

import edu.dosw.application.ports.SellerUseCases.CreateSellerUseCase;
import edu.dosw.application.dto.command.SellerCommands.CreateSellerCommand;
import edu.dosw.application.dto.SellerDTO;
import edu.dosw.domain.model.*;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.SellerRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
import edu.dosw.utils.IdGenerator;
import edu.dosw.infrastructure.web.mappers.SellerWebMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateSellerService implements CreateSellerUseCase {

    private final SellerRepositoryPort sellerRepository;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final IdGenerator idGenerator;
    private final SellerWebMapper sellerWebMapper;

    @Override
    public SellerDTO createSeller(CreateSellerCommand command) {
        Email email = new Email(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Seller with this email already exists");
        }

        String encodedPassword = passwordEncoder.encode(command.password());

        Seller seller = new Seller(
                new UserId(idGenerator.generateUniqueId()),
                new IdentityDocument(command.identityDocument()),
                email,
                new FullName(command.fullName()),
                new PasswordHash(encodedPassword),
                command.companyName(),
                command.businessAddress()
        );

        Seller savedSeller = sellerRepository.save(seller);
        userRepository.save(savedSeller);

        return sellerWebMapper.toDTO(savedSeller);
    }
}