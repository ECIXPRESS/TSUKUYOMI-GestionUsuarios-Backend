package edu.dosw.application.ports.SellerUseCases;


import edu.dosw.domain.model.ValueObject.UserId;

public interface DeleteSellerUseCase {
    void deleteSeller(UserId sellerId);
}