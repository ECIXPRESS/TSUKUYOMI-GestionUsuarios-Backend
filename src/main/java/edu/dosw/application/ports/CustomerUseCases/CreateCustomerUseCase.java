package edu.dosw.application.ports.CustomerUseCases;

import edu.dosw.application.dto.command.CustomerCommands.CreateCustomerCommand;
import edu.dosw.application.dto.CustomerDTO;

public interface CreateCustomerUseCase {
    CustomerDTO createCustomer(CreateCustomerCommand command);
}