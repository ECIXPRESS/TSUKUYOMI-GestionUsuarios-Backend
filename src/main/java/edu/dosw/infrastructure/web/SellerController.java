package edu.dosw.infrastructure.web;

import edu.dosw.application.ports.SellerUseCases.*;
import edu.dosw.application.dto.command.SellerCommands.CreateSellerCommand;
import edu.dosw.application.dto.command.SellerCommands.UpdateSellerCommand;
import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.application.dto.SellerDTO;
import edu.dosw.application.dto.SellerUpdateDTO;
import edu.dosw.infrastructure.web.mappers.SellerWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final CreateSellerUseCase createSellerUseCase;
    private final GetSellerUseCase getSellerUseCase;
    private final GetAllSellersUseCase getAllSellersUseCase;
    private final UpdateSellerUseCase updateSellerUseCase;
    private final DeleteSellerUseCase deleteSellerUseCase;
    private final SellerWebMapper sellerWebMapper;

    @PostMapping
    public ResponseEntity<SellerDTO> createSeller(@RequestBody SellerDTO sellerDTO) {
        CreateSellerCommand command = sellerWebMapper.toCommand(sellerDTO);
        SellerDTO result = createSellerUseCase.createSeller(command);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<SellerDTO> getSeller(@PathVariable String sellerId) {
        SellerDTO seller = getSellerUseCase.getSellerById(new UserId(sellerId));
        return ResponseEntity.ok(seller);
    }

    @GetMapping
    public ResponseEntity<List<Seller>> getAllSellers() {
        List<Seller> sellers = getAllSellersUseCase.getAllSellers();
        return ResponseEntity.ok(sellers);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Seller>> getPendingSellers() {
        List<Seller> sellers = getAllSellersUseCase.getPendingSellers();
        return ResponseEntity.ok(sellers);
    }

    @PutMapping("/{sellerId}")
    public ResponseEntity<SellerUpdateDTO> updateSeller(@PathVariable String sellerId,
                                                        @RequestBody SellerUpdateDTO sellerUpdateDTO) {
        UpdateSellerCommand command = sellerWebMapper.toCommand(sellerUpdateDTO);
        SellerUpdateDTO result = updateSellerUseCase.updateSeller(new UserId(sellerId), command);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{sellerId}")
    public ResponseEntity<Void> deleteSeller(@PathVariable String sellerId) {
        deleteSellerUseCase.deleteSeller(new UserId(sellerId));
        return ResponseEntity.noContent().build();
    }
}