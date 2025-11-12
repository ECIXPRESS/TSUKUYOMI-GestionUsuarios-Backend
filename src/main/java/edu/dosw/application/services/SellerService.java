package edu.dosw.application.services;

import edu.dosw.domain.model.Seller;
import edu.dosw.domain.ports.SellerRepository;
import edu.dosw.dto.SellerDTO;
import edu.dosw.dto.SellerUpdateDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.utils.IdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@AllArgsConstructor
@Service
public class SellerService {
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdGenerator idGenerator;

    public Seller getSellerById(String sellerId) {
        return sellerRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
    }

    public Seller createSeller(SellerDTO request) {
        if (request.identityDocument() == null || request.email() == null || request.fullName() == null || request.password() == null) {
            throw new IllegalArgumentException("Seller data is incomplete");
        }

        if (sellerRepository.existsByEmail(request.email())) {
            throw new BusinessException("Seller with this email already exists");
        }

        String userId = idGenerator.generateUniqueId();
        String passwordHash = passwordEncoder.encode(request.password());

        Seller seller = new Seller.SellerBuilder()
                .userId(userId)
                .identityDocument(request.identityDocument())
                .email(request.email())
                .fullName(request.fullName())
                .passwordHash(passwordHash)
                .companyName(request.companyName())
                .businessAddress(request.businessAddress())
                .build();

        return sellerRepository.save(seller);
    }

    public Seller updateSeller(String sellerId, SellerUpdateDTO updateDTO) {
        Seller seller = sellerRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        if (updateDTO.email() != null && !updateDTO.email().equals(seller.getEmail())) {
            if (sellerRepository.existsByEmail(updateDTO.email())) {
                throw new BusinessException("Email already exists");
            }
            seller.setEmail(updateDTO.email());
        }
        if (updateDTO.identityDocument() != null) {
            seller.setIdentityDocument(updateDTO.identityDocument());
        }
        if (updateDTO.fullName() != null) {
            seller.setFullName(updateDTO.fullName());
        }
        if (updateDTO.companyName() != null) {
            seller.setCompanyName(updateDTO.companyName());
        }
        if (updateDTO.businessAddress() != null) {
            seller.setBusinessAddress(updateDTO.businessAddress());
        }
        return sellerRepository.save(seller);
    }

    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public List<Seller> getPendingSellers() {
        return sellerRepository.findByApproved(false);
    }
    public void deleteSeller(String sellerId) {
        Seller seller = sellerRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        sellerRepository.delete(seller);
    }

}
