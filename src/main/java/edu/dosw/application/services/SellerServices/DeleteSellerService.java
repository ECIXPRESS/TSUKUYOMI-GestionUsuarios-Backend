package edu.dosw.application.services.SellerServices;

import edu.dosw.application.ports.SellerUseCases.DeleteSellerUseCase;
import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.ports.SellerRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteSellerService implements DeleteSellerUseCase {

    private final SellerRepositoryPort sellerRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public void deleteSeller(UserId sellerId) {
        Seller seller = sellerRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        sellerRepository.delete(seller);
        userRepository.deleteByUserId(sellerId);
    }
}