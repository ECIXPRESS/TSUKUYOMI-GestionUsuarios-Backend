package edu.dosw.application.ports.CustomerUseCases;

import edu.dosw.domain.model.ValueObject.UserId;

public interface DeleteCustomerUseCase {
    void deleteCustomer(UserId customerId);
}