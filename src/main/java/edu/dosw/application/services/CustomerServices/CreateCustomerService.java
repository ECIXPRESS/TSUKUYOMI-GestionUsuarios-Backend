package edu.dosw.application.services.CustomerServices;


import edu.dosw.application.ports.CustomerUseCases.CreateCustomerUseCase;
import edu.dosw.application.dto.command.CustomerCommands.CreateCustomerCommand;
import edu.dosw.application.dto.CustomerDTO;
import edu.dosw.domain.model.*;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.CustomerRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.utils.IdGenerator;
import edu.dosw.infrastructure.web.mappers.CustomerWebMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateCustomerService implements CreateCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final IdGenerator idGenerator;
    private final CustomerWebMapper customerWebMapper;

    @Override
    public CustomerDTO createCustomer(CreateCustomerCommand command) {
        Email email = new Email(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Customer with this email already exists");
        }

        String encodedPassword = passwordEncoder.encode(command.password());

        Customer customer = new Customer(
                new UserId(idGenerator.generateUniqueId()),
                new IdentityDocument(command.identityDocument()),
                email,
                new FullName(command.fullName()),
                new PasswordHash(encodedPassword),
                command.phoneNumber()
        );
        Customer savedCustomer = customerRepository.save(customer);
        userRepository.save(savedCustomer);
        return customerWebMapper.toDTO(savedCustomer);
    }
}