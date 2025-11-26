package edu.dosw.application.services.SellerServices;


import edu.dosw.application.ports.SellerUseCases.GetSellerUseCase;
import edu.dosw.application.dto.SellerDTO;
import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.ports.SellerRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.infrastructure.web.mappers.SellerWebMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetSellerService implements GetSellerUseCase {

    private final SellerRepositoryPort sellerRepository;
    private final SellerWebMapper sellerWebMapper;

    @Override
    public SellerDTO getSellerById(UserId sellerId) {
        Seller seller = sellerRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        return sellerWebMapper.toDTO(seller);
    }
}