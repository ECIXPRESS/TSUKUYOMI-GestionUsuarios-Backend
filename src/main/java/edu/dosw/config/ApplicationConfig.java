package edu.dosw.config;

import edu.dosw.application.ports.*;
import edu.dosw.application.ports.CustomerUseCases.*;
import edu.dosw.application.ports.UserUseCase.GetUserCredentialsUseCase;
import edu.dosw.application.ports.AdminUseCases.*;
import edu.dosw.application.ports.SellerUseCases.*;
import edu.dosw.application.services.*;
import edu.dosw.application.services.CustomerServices.*;
import edu.dosw.application.services.UserServices.GetUserCredentialsService;
import edu.dosw.application.services.AdminServices.*;
import edu.dosw.application.services.SellerServices.*;
import edu.dosw.domain.ports.CustomerRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.domain.ports.AdminRepositoryPort;
import edu.dosw.domain.ports.SellerRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
import edu.dosw.utils.IdGenerator;
import edu.dosw.infrastructure.web.mappers.CustomerWebMapper;
import edu.dosw.infrastructure.web.mappers.UserWebMapper;
import edu.dosw.infrastructure.web.mappers.AdminWebMapper;
import edu.dosw.infrastructure.web.mappers.SellerWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final CustomerRepositoryPort customerRepository;
    private final UserRepositoryPort userRepository;
    private final AdminRepositoryPort adminRepository;
    private final SellerRepositoryPort sellerRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final IdGenerator idGenerator;
    private final CustomerWebMapper customerWebMapper;
    private final UserWebMapper userWebMapper;
    private final AdminWebMapper adminWebMapper;
    private final SellerWebMapper sellerWebMapper;


    @Bean
    public CreateCustomerUseCase createCustomerUseCase() {
        return new CreateCustomerService(customerRepository, userRepository, passwordEncoder, idGenerator, customerWebMapper);
    }

    @Bean
    public GetCustomerUseCase getCustomerUseCase() {
        return new GetCustomerService(customerRepository, customerWebMapper);
    }

    @Bean
    public UpdateCustomerUseCase updateCustomerUseCase() {
        return new UpdateCustomerService(customerRepository, userRepository, customerWebMapper);
    }

    @Bean
    public DeleteCustomerUseCase deleteCustomerUseCase() {
        return new DeleteCustomerService(customerRepository, userRepository);
    }


    @Bean
    public UpdatePasswordUseCase updatePasswordUseCase() {
        return new UpdatePasswordService(userRepository, passwordEncoder);
    }

    @Bean
    public GetUserCredentialsUseCase getUserCredentialsUseCase() {
        return new GetUserCredentialsService(userRepository, passwordEncoder, userWebMapper);
    }


    @Bean
    public CreateAdminUseCase createAdminUseCase() {
        return new CreateAdminService(adminRepository, userRepository, passwordEncoder, idGenerator, adminWebMapper);
    }

    @Bean
    public GetAdminUseCase getAdminUseCase() {
        return new GetAdminService(adminRepository, adminWebMapper);
    }

    @Bean
    public UpdateAdminUseCase updateAdminUseCase() {
        return new UpdateAdminService(adminRepository, userRepository, adminWebMapper);
    }

    @Bean
    public DeleteAdminUseCase deleteAdminUseCase() {
        return new DeleteAdminService(adminRepository, userRepository);
    }


    @Bean
    public CreateSellerUseCase createSellerUseCase() {
        return new CreateSellerService(sellerRepository, userRepository, passwordEncoder, idGenerator, sellerWebMapper);
    }

    @Bean
    public GetSellerUseCase getSellerUseCase() {
        return new GetSellerService(sellerRepository, sellerWebMapper);
    }

    @Bean
    public GetAllSellersUseCase getAllSellersUseCase() {
        return new GetAllSellersService(sellerRepository);
    }

    @Bean
    public UpdateSellerUseCase updateSellerUseCase() {
        return new UpdateSellerService(sellerRepository, userRepository, sellerWebMapper);
    }

    @Bean
    public DeleteSellerUseCase deleteSellerUseCase() {
        return new DeleteSellerService(sellerRepository, userRepository);
    }
}