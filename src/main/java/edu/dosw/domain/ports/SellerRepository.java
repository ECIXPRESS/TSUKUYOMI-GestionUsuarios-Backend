package edu.dosw.domain.ports;

import edu.dosw.domain.model.Seller;
import java.util.List;
import java.util.Optional;

public interface SellerRepository {
    Seller save(Seller seller);
    Optional<Seller> findByUserId(String userId);
    Optional<Seller> findByEmail(String email);
    List<Seller> findAll();
    List<Seller> findByApproved(boolean approved);
    boolean existsByEmail(String email);
    void delete(Seller seller);
}