package edu.dosw.infrastructure.web.mappers;

import edu.dosw.application.dto.command.CustomerCommands.CreateCustomerCommand;
import edu.dosw.application.dto.command.CustomerCommands.UpdateCustomerCommand;
import edu.dosw.domain.model.Customer;
import edu.dosw.application.dto.CustomerDTO;
import edu.dosw.application.dto.CustomerUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class CustomerWebMapper {

    public CreateCustomerCommand toCommand(CustomerDTO dto) {
        return new CreateCustomerCommand(
                dto.identityDocument(),
                dto.email(),
                dto.fullName(),
                dto.password(),
                dto.phoneNumber()
        );
    }

    public UpdateCustomerCommand toCommand(CustomerUpdateDTO dto) {
        return new UpdateCustomerCommand(
                dto.identityDocument(),
                dto.email(),
                dto.fullName(),
                dto.phoneNumber()
        );
    }

    public CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(
                customer.getEmail().value(),
                customer.getFullName().value(),
                "",
                customer.getIdentityDocument().value(),
                customer.getPhoneNumber()
        );
    }

    public CustomerUpdateDTO toUpdateDTO(Customer customer) {
        return new CustomerUpdateDTO(
                customer.getIdentityDocument().value(),
                customer.getEmail().value(),
                customer.getFullName().value(),
                customer.getPhoneNumber()
        );
    }
}