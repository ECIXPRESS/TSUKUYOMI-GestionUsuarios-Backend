package edu.dosw.utils;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class IdGenerator {

    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}