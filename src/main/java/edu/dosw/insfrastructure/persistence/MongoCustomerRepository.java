package edu.dosw.insfrastructure.persistence;

import edu.dosw.domain.model.Customer;
import edu.dosw.domain.ports.CustomerRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MongoCustomerRepository extends MongoRepository<Customer, String>, CustomerRepository {
    @Query("{ 'email': ?0 }")
    Optional<Customer> findByEmail(String email);

    @Query("{ 'userId': ?0 }")
    Optional<Customer> findByUserId(String userId);

    @Query(value = "{ 'email': ?0 }", exists = true)
    boolean existsByEmail(String email);
}