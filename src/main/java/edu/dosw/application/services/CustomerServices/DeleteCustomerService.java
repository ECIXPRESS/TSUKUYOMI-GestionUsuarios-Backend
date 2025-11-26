package edu.dosw.application.services.CustomerServices;


import edu.dosw.application.ports.CustomerUseCases.DeleteCustomerUseCase;
import edu.dosw.domain.model.Customer;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.ports.CustomerRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
public class DeleteCustomerService implements DeleteCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public void deleteCustomer(UserId customerId) {
        Customer customer = customerRepository.findByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customerRepository.delete(customer);
        userRepository.deleteByUserId(customerId);
    }
}