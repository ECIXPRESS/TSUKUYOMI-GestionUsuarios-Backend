package edu.dosw.application.services.SellerServices;


import edu.dosw.application.ports.SellerUseCases.GetAllSellersUseCase;
import edu.dosw.domain.model.Seller;
import edu.dosw.domain.ports.SellerRepositoryPort;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class GetAllSellersService implements GetAllSellersUseCase {

    private final SellerRepositoryPort sellerRepository;

    @Override
    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    @Override
    public List<Seller> getPendingSellers() {
        return sellerRepository.findByApproved(false);
    }
}