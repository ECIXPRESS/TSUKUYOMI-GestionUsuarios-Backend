package edu.dosw.domain.ports;


import edu.dosw.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findByUserId(String userId);
    Optional<Customer> findByEmail(String email);
    List<Customer> findAll();
    boolean existsByEmail(String email);
    void delete(Customer customer);
}