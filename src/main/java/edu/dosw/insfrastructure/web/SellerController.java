package edu.dosw.insfrastructure.web;
import edu.dosw.application.services.SellerService;
import edu.dosw.domain.model.Seller;
import edu.dosw.dto.SellerDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users/sellers")
@AllArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody SellerDTO sellerDTO) {
        Seller seller = sellerService.createSeller(sellerDTO);
        return ResponseEntity.ok(seller);
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<Seller> getSeller(@PathVariable String sellerId) {
        Seller seller = sellerService.getSellerById(sellerId);
        return ResponseEntity.ok(seller);
    }

    @GetMapping
    public ResponseEntity<List<Seller>> getAllSellers() {
        List<Seller> sellers = sellerService.getAllSellers();
        return ResponseEntity.ok(sellers);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Seller>> getPendingSellers() {
        List<Seller> sellers = sellerService.getPendingSellers();
        return ResponseEntity.ok(sellers);
    }



    @DeleteMapping("/{sellerId}")
    public ResponseEntity<Void> deleteSeller(@PathVariable String sellerId) {
        sellerService.deleteSeller(sellerId);
        return ResponseEntity.noContent().build();
    }
}