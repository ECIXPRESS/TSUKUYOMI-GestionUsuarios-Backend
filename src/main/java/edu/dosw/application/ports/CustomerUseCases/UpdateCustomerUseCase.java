package edu.dosw.application.ports.CustomerUseCases;


import edu.dosw.application.dto.command.CustomerCommands.UpdateCustomerCommand;
import edu.dosw.application.dto.CustomerUpdateDTO;
import edu.dosw.domain.model.ValueObject.UserId;

public interface UpdateCustomerUseCase {
    CustomerUpdateDTO updateCustomer(UserId customerId, UpdateCustomerCommand command);
}