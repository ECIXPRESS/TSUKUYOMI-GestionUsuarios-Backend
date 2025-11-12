package edu.dosw.insfrastructure.persistence;

import edu.dosw.domain.model.User;
import edu.dosw.domain.ports.UserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MongoUserRepository extends MongoRepository<User, String>, UserRepository {

    @Query("{ 'email': ?0 }")
    Optional<User> findByEmail(String email);

    @Query("{ 'userId': ?0 }")
    Optional<User> findByUserId(String userId);

    @Query(value = "{ 'email': ?0 }", exists = true)
    boolean existsByEmail(String email);

    @Query("{ 'userId': ?0 }")
    void deleteByUserId(String userId);
}