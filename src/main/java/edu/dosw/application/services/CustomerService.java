package edu.dosw.application.services;

import edu.dosw.domain.model.Customer;
import edu.dosw.domain.ports.CustomerRepository;
import edu.dosw.dto.CustomerDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.utils.IdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdGenerator idGenerator;

    public Customer getCustomerById(String customerId) {
        return customerRepository.findByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    public Customer createCustomer(CustomerDTO request) {
        if (request.identityDocument() == null || request.email() == null || request.fullName() == null || request.password() == null) {
            throw new IllegalArgumentException("Customer data is incomplete");
        }
        if (customerRepository.existsByEmail(request.email())) {
            throw new BusinessException("Customer with this email already exists");
        }

        String userId = idGenerator.generateUniqueId();
        String passwordHash = passwordEncoder.encode(request.password());

        Customer customer = new Customer.CustomerBuilder()
                .userId(userId)
                .identityDocument(request.identityDocument())
                .email(request.email())
                .fullName(request.fullName())
                .passwordHash(passwordHash)
                .phoneNumber(request.phoneNumber())
                .build();

        return customerRepository.save(customer);
    }



    public void updatePassword(String customerId, String newPassword) {
        Customer customer = customerRepository.findByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        String newPasswordHash = passwordEncoder.encode(newPassword);
        customer.setPasswordHash(newPasswordHash);
        customerRepository.save(customer);
    }

    public void deleteCustomer(String customerId) {
        Customer customer = customerRepository.findByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customerRepository.delete(customer);
    }
}