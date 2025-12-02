package edu.dosw.infrastructure.persistence;


import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.model.ValueObject.Email;
import edu.dosw.domain.ports.SellerRepositoryPort;
import edu.dosw.infrastructure.persistence.documents.SellerDocument;
import edu.dosw.infrastructure.persistence.mappers.SellerMongoMapper;
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
public class MongoSellerRepository implements SellerRepositoryPort {

    private final MongoTemplate mongoTemplate;
    private final SellerMongoMapper sellerMapper;

    @Override
    public Seller save(Seller seller) {
        SellerDocument document = sellerMapper.toDocument(seller);
        SellerDocument saved = mongoTemplate.save(document);
        return sellerMapper.toDomain(saved);
    }

    @Override
    public Optional<Seller> findByUserId(UserId userId) {
        Query query = new Query(Criteria.where("userId").is(userId.value()));
        SellerDocument document = mongoTemplate.findOne(query, SellerDocument.class);
        return Optional.ofNullable(sellerMapper.toDomain(document));
    }

    @Override
    public Optional<Seller> findByEmail(Email email) {
        Query query = new Query(Criteria.where("email").is(email.value()));
        SellerDocument document = mongoTemplate.findOne(query, SellerDocument.class);
        return Optional.ofNullable(sellerMapper.toDomain(document));
    }

    @Override
    public List<Seller> findAll() {
        List<SellerDocument> documents = mongoTemplate.findAll(SellerDocument.class);
        return documents.stream()
                .map(sellerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Seller> findByApproved(boolean approved) {
        Query query = new Query(Criteria.where("approved").is(approved));
        List<SellerDocument> documents = mongoTemplate.find(query, SellerDocument.class);
        return documents.stream()
                .map(sellerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(Email email) {
        Query query = new Query(Criteria.where("email").is(email.value()));
        return mongoTemplate.exists(query, SellerDocument.class);
    }

    @Override
    public void delete(Seller seller) {
        SellerDocument document = sellerMapper.toDocument(seller);
        mongoTemplate.remove(document);
    }
}