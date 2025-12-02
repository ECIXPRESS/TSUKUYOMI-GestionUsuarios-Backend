package edu.dosw.application.ports.SellerUseCases;


import edu.dosw.domain.model.Seller;
import java.util.List;

public interface GetAllSellersUseCase {
    List<Seller> getAllSellers();
    List<Seller> getPendingSellers();
}