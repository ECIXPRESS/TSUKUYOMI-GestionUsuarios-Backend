package edu.dosw.application.ports.CustomerUseCases;


import edu.dosw.application.dto.CustomerDTO;
import edu.dosw.domain.model.ValueObject.UserId;

public interface GetCustomerUseCase {
    CustomerDTO getCustomerById(UserId customerId);
}