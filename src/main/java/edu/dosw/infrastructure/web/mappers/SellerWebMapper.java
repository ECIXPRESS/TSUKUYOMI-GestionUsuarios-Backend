package edu.dosw.infrastructure.web.mappers;

import edu.dosw.application.dto.command.SellerCommands.CreateSellerCommand;
import edu.dosw.application.dto.command.SellerCommands.UpdateSellerCommand;
import edu.dosw.domain.model.Seller;
import edu.dosw.application.dto.SellerDTO;
import edu.dosw.application.dto.SellerUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class SellerWebMapper {

    public CreateSellerCommand toCommand(SellerDTO dto) {
        return new CreateSellerCommand(
                dto.identityDocument(),
                dto.email(),
                dto.fullName(),
                dto.password(),
                dto.companyName(),
                dto.businessAddress()
        );
    }

    public UpdateSellerCommand toCommand(SellerUpdateDTO dto) {
        return new UpdateSellerCommand(
                dto.identityDocument(),
                dto.email(),
                dto.fullName(),
                dto.companyName(),
                dto.businessAddress()
        );
    }

    public SellerDTO toDTO(Seller seller) {
        return new SellerDTO(
                seller.getEmail().value(),
                seller.getFullName().value(),
                "",
                seller.getIdentityDocument().value(),
                seller.getCompanyName(),
                seller.getBusinessAddress()
        );
    }

    public SellerUpdateDTO toUpdateDTO(Seller seller) {
        return new SellerUpdateDTO(
                seller.getIdentityDocument().value(),
                seller.getEmail().value(),
                seller.getFullName().value(),
                seller.getCompanyName(),
                seller.getBusinessAddress()
        );
    }
}