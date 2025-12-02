package edu.dosw.domain.ports;

import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.model.ValueObject.Email;
import java.util.List;
import java.util.Optional;

public interface SellerRepositoryPort {
    Seller save(Seller seller);
    Optional<Seller> findByUserId(UserId userId);
    Optional<Seller> findByEmail(Email email);
    List<Seller> findAll();
    List<Seller> findByApproved(boolean approved);
    boolean existsByEmail(Email email);
    void delete(Seller seller);
}