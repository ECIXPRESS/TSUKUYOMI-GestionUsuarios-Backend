package edu.dosw.infrastructure.persistence;


import edu.dosw.domain.model.Customer;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.model.ValueObject.Email;
import edu.dosw.domain.ports.CustomerRepositoryPort;
import edu.dosw.infrastructure.persistence.documents.CustomerDocument;
import edu.dosw.infrastructure.persistence.mappers.CustomerMongoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MongoCustomerRepository implements CustomerRepositoryPort {

    private final MongoTemplate mongoTemplate;
    private final CustomerMongoMapper customerMapper;

    @Override
    public Customer save(Customer customer) {
        CustomerDocument document = customerMapper.toDocument(customer);
        CustomerDocument saved = mongoTemplate.save(document);
        return customerMapper.toDomain(saved);
    }

    @Override
    public Optional<Customer> findByUserId(UserId userId) {
        Query query = new Query(Criteria.where("userId").is(userId.value()));
        CustomerDocument document = mongoTemplate.findOne(query, CustomerDocument.class);
        return Optional.ofNullable(customerMapper.toDomain(document));
    }

    @Override
    public boolean existsByEmail(Email email) {
        Query query = new Query(Criteria.where("email").is(email.value()));
        return mongoTemplate.exists(query, CustomerDocument.class);
    }

    @Override
    public void delete(Customer customer) {
        CustomerDocument document = customerMapper.toDocument(customer);
        mongoTemplate.remove(document);
    }
}