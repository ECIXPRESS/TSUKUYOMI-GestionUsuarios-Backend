package edu.dosw.application.services.CustomerServices;
;

import edu.dosw.application.ports.CustomerUseCases.GetCustomerUseCase;
import edu.dosw.application.dto.CustomerDTO;
import edu.dosw.domain.model.Customer;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.ports.CustomerRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.infrastructure.web.mappers.CustomerWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
public class GetCustomerService implements GetCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerWebMapper customerWebMapper;

    @Override
    public CustomerDTO getCustomerById(UserId customerId) {
        Customer customer = customerRepository.findByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return customerWebMapper.toDTO(customer);
    }
}