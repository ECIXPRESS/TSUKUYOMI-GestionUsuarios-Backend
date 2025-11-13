package edu.dosw.application.ports;


import edu.dosw.application.dto.command.UpdatePasswordCommand;
import edu.dosw.application.dto.PasswordUpdateRequestDTO;

public interface UpdatePasswordUseCase {
    PasswordUpdateRequestDTO updatePassword(UpdatePasswordCommand command);
}