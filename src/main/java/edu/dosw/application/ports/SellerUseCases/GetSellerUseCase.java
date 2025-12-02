package edu.dosw.application.ports.SellerUseCases;

import edu.dosw.application.dto.SellerDTO;
import edu.dosw.domain.model.ValueObject.UserId;

public interface GetSellerUseCase {
    SellerDTO getSellerById(UserId sellerId);
}