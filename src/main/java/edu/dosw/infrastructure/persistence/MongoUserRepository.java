package edu.dosw.infrastructure.persistence;

import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.model.ValueObject.Email;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.infrastructure.persistence.documents.UserDocument;
import edu.dosw.infrastructure.persistence.mappers.UserMongoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MongoUserRepository implements UserRepositoryPort {

    private final MongoTemplate mongoTemplate;
    private final UserMongoMapper userMapper;

    @Override
    public User save(User user) {
        UserDocument document = userMapper.toDocument(user);
        UserDocument saved = mongoTemplate.save(document);
        return userMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        Query query = new Query(Criteria.where("email").is(email.value()));
        UserDocument document = mongoTemplate.findOne(query, UserDocument.class);
        return Optional.ofNullable(userMapper.toDomain(document));
    }

    @Override
    public Optional<User> findByUserId(UserId userId) {
        Query query = new Query(Criteria.where("userId").is(userId.value()));
        UserDocument document = mongoTemplate.findOne(query, UserDocument.class);
        return Optional.ofNullable(userMapper.toDomain(document));
    }

    @Override
    public boolean existsByEmail(Email email) {
        Query query = new Query(Criteria.where("email").is(email.value()));
        return mongoTemplate.exists(query, UserDocument.class);
    }

    @Override
    public void deleteByUserId(UserId userId) {
        Query query = new Query(Criteria.where("userId").is(userId.value()));
        mongoTemplate.remove(query, UserDocument.class);
    }
}