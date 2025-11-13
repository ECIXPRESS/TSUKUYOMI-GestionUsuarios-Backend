package edu.dosw.application.services.CustomerServices;

import edu.dosw.application.ports.CustomerUseCases.UpdateCustomerUseCase;
import edu.dosw.application.dto.command.CustomerCommands.UpdateCustomerCommand;
import edu.dosw.application.dto.CustomerUpdateDTO;
import edu.dosw.domain.model.Customer;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.CustomerRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.infrastructure.web.mappers.CustomerWebMapper;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class UpdateCustomerService implements UpdateCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final UserRepositoryPort userRepository;
    private final CustomerWebMapper customerWebMapper;

    @Override
    public CustomerUpdateDTO updateCustomer(UserId customerId, UpdateCustomerCommand command) {
        Customer customer = customerRepository.findByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (command.email() != null && !command.email().equals(customer.getEmail().value())) {
            Email newEmail = new Email(command.email());
            if (userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email already exists");
            }
        }
        Customer updatedCustomer = new Customer(
                customer.getUserId(),
                command.identityDocument() != null ?
                        new IdentityDocument(command.identityDocument()) : customer.getIdentityDocument(),
                command.email() != null ?
                        new Email(command.email()) : customer.getEmail(),
                command.fullName() != null ?
                        new FullName(command.fullName()) : customer.getFullName(),
                customer.getPasswordHash(),
                command.phoneNumber() != null ? command.phoneNumber() : customer.getPhoneNumber()
        );
        Customer savedCustomer = customerRepository.save(updatedCustomer);
        userRepository.save(savedCustomer);
        return customerWebMapper.toUpdateDTO(savedCustomer);
    }
}