package com.example.minierp.domain.shared;

public interface DomainEventPublisher {
    void publish(Object event);
}
