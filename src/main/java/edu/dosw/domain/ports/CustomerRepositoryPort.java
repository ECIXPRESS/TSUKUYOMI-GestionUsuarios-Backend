package edu.dosw.domain.ports;

import edu.dosw.domain.model.Customer;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.model.ValueObject.Email;
import java.util.Optional;

public interface CustomerRepositoryPort {
    Customer save(Customer customer);
    Optional<Customer> findByUserId(UserId userId);
    boolean existsByEmail(Email email);
    void delete(Customer customer);
}