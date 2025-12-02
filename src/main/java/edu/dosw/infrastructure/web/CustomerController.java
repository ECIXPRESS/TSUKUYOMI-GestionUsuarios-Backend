package edu.dosw.infrastructure.web;

import edu.dosw.application.ports.CustomerUseCases.CreateCustomerUseCase;
import edu.dosw.application.ports.CustomerUseCases.GetCustomerUseCase;
import edu.dosw.application.ports.CustomerUseCases.UpdateCustomerUseCase;
import edu.dosw.application.ports.UpdatePasswordUseCase;
import edu.dosw.application.ports.CustomerUseCases.DeleteCustomerUseCase;
import edu.dosw.application.dto.command.UpdatePasswordCommand;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.application.dto.CustomerDTO;
import edu.dosw.application.dto.CustomerUpdateDTO;
import edu.dosw.application.dto.PasswordUpdateRequestDTO;
import edu.dosw.infrastructure.web.mappers.CustomerWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final CustomerWebMapper customerWebMapper;

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        var command = customerWebMapper.toCommand(customerDTO);
        CustomerDTO result = createCustomerUseCase.createCustomer(command);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable String customerId) {
        CustomerDTO customer = getCustomerUseCase.getCustomerById(new UserId(customerId));
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{customerId}/password")
    public ResponseEntity<PasswordUpdateRequestDTO> updatePassword(@PathVariable String customerId,
                                                                   @RequestBody PasswordUpdateRequestDTO request) {
        var command = new UpdatePasswordCommand(new UserId(customerId), request.newPassword());
        PasswordUpdateRequestDTO result = updatePasswordUseCase.updatePassword(command);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerUpdateDTO> updateCustomer(@PathVariable String customerId,
                                                            @RequestBody CustomerUpdateDTO customerUpdateDTO) {
        var command = customerWebMapper.toCommand(customerUpdateDTO);
        CustomerUpdateDTO result = updateCustomerUseCase.updateCustomer(new UserId(customerId), command);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        deleteCustomerUseCase.deleteCustomer(new UserId(customerId));
        return ResponseEntity.noContent().build();
    }
}