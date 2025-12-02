package edu.dosw.application.ports.SellerUseCases;


import edu.dosw.application.dto.command.SellerCommands.UpdateSellerCommand;
import edu.dosw.application.dto.SellerUpdateDTO;
import edu.dosw.domain.model.ValueObject.UserId;

public interface UpdateSellerUseCase {
    SellerUpdateDTO updateSeller(UserId sellerId, UpdateSellerCommand command);
}