package edu.dosw.infrastructure.persistence;


import edu.dosw.domain.model.Admin;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.model.ValueObject.Email;
import edu.dosw.domain.ports.AdminRepositoryPort;
import edu.dosw.infrastructure.persistence.documents.AdminDocument;
import edu.dosw.infrastructure.persistence.mappers.AdminMongoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MongoAdminRepository implements AdminRepositoryPort {

    private final MongoTemplate mongoTemplate;
    private final AdminMongoMapper adminMapper;

    @Override
    public Admin save(Admin admin) {
        AdminDocument document = adminMapper.toDocument(admin);
        AdminDocument saved = mongoTemplate.save(document);
        return adminMapper.toDomain(saved);
    }

    @Override
    public Optional<Admin> findByUserId(UserId userId) {
        Query query = new Query(Criteria.where("userId").is(userId.value()));
        AdminDocument document = mongoTemplate.findOne(query, AdminDocument.class);
        return Optional.ofNullable(adminMapper.toDomain(document));
    }

    @Override
    public Optional<Admin> findByEmail(Email email) {
        Query query = new Query(Criteria.where("email").is(email.value()));
        AdminDocument document = mongoTemplate.findOne(query, AdminDocument.class);
        return Optional.ofNullable(adminMapper.toDomain(document));
    }

    @Override
    public List<Admin> findAll() {
        List<AdminDocument> documents = mongoTemplate.findAll(AdminDocument.class);
        return documents.stream()
                .map(adminMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(Email email) {
        Query query = new Query(Criteria.where("email").is(email.value()));
        return mongoTemplate.exists(query, AdminDocument.class);
    }

    @Override
    public void delete(Admin admin) {
        AdminDocument document = adminMapper.toDocument(admin);
        mongoTemplate.remove(document);
    }
}