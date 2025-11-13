package edu.dosw.application.ports.UserUseCase;


import edu.dosw.application.dto.UserCredentialsDTO;
import edu.dosw.domain.model.ValueObject.Email;

public interface GetUserCredentialsUseCase {
    UserCredentialsDTO getCredentialsByEmail(Email email);
    UserCredentialsDTO getUserByEmailAndPassword(Email email, String password);
}