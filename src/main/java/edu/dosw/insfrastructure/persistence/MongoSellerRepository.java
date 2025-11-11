package edu.dosw.insfrastructure.persistence;

import edu.dosw.domain.model.Seller;
import edu.dosw.domain.ports.SellerRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MongoSellerRepository extends MongoRepository<Seller, String>, SellerRepository {
    @Query("{ 'email': ?0 }")
    Optional<Seller> findByEmail(String email);

    @Query("{ 'userId': ?0 }")
    Optional<Seller> findByUserId(String userId);

    @Query("{ 'approved': ?0 }")
    List<Seller> findByApproved(boolean approved);

    @Query(value = "{ 'email': ?0 }", exists = true)
    boolean existsByEmail(String email);
}