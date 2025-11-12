package edu.dosw.insfrastructure.web;
import edu.dosw.application.services.CustomerService;
import edu.dosw.domain.model.Customer;
import edu.dosw.dto.CustomerDTO;
import edu.dosw.dto.CustomerUpdateDTO;
import edu.dosw.dto.PasswordUpdateRequestDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/customers")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDTO customerDTO) {
        Customer customer = customerService.createCustomer(customerDTO);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomer(@PathVariable String customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }


    @PutMapping("/{customerId}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable String customerId, @RequestBody PasswordUpdateRequestDTO request) {
        customerService.updatePassword(customerId, request.newPassword());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String customerId, @RequestBody CustomerUpdateDTO customerUpdateDTO) {
        Customer customer = customerService.updateCustomer(customerId, customerUpdateDTO);
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}