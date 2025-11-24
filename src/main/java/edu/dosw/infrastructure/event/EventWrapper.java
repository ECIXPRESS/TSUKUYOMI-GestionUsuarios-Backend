package edu.dosw.infrastructure.event;

import lombok.Data;

@Data
public class EventWrapper {
    private String eventId;
    private String eventType;
    private String timestamp;
    private String version;
    private Object data;
}