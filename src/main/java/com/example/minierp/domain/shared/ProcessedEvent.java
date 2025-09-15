package com.example.minierp.domain.shared;

import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "processed_events")
public class ProcessedEvent {
    @Id private String eventId;
    private LocalDateTime processedAt;
    public ProcessedEvent() {

    }
    public ProcessedEvent(String eventId, LocalDateTime processedAt) {
        this.eventId = eventId;
        this.processedAt = processedAt;
    }
}


