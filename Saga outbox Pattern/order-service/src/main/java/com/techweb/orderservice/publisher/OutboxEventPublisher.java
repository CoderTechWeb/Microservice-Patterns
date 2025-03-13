package com.techweb.orderservice.publisher;

import com.techweb.orderservice.entity.OutboxEvent;
import com.techweb.orderservice.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxEventPublisher {

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // Poll the Outbox table for unprocessed events
    @Scheduled(fixedRate = 5000) // Run every 5 seconds (can be adjusted)
    public void publishOutboxEvents() {
        List<OutboxEvent> unprocessedEvents = outboxRepository.findByProcessedFalse();

        for (OutboxEvent event : unprocessedEvents) {
            try {
                // Send the event to Kafka
                kafkaTemplate.send(event.getEventType(), event.getPayload());

                // Mark the event as processed in the database after successful delivery
                event.setProcessed(true);
                outboxRepository.save(event);

            } catch (Exception e) {

            }
        }
    }
}