package edu.dosw.infrastructure.web.mappers;


import edu.dosw.domain.model.User;
import edu.dosw.application.dto.UserCredentialsDTO;
import org.springframework.stereotype.Component;

@Component
public class UserWebMapper {

    public UserCredentialsDTO toCredentialsDTO(User user) {
        return new UserCredentialsDTO(
                user.getUserId().value(),
                user.getUserId().value(),
                user.getEmail().value(),
                user.getPasswordHash().value(),
                user.getRole()
        );
    }
}