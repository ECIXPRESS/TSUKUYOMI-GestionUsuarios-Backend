package edu.dosw.application.ports.SellerUseCases;

import edu.dosw.application.dto.command.SellerCommands.CreateSellerCommand;
import edu.dosw.application.dto.SellerDTO;

public interface CreateSellerUseCase {
    SellerDTO createSeller(CreateSellerCommand command);
}