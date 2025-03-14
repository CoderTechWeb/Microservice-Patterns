package com.techweb.orderservice.poller;

import com.techweb.orderservice.entity.OutboxEvent;
import com.techweb.orderservice.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxPoller {
    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRateString = "${scheduler.outbox.interval}")
    public void publishOutboxEvents() {
        List<OutboxEvent> events = outboxRepository.findByProcessedFalse();
        for (OutboxEvent event : events) {
            kafkaTemplate.send(event.getEventType(), event.getPayload());
            event.setProcessed(true);
            outboxRepository.save(event);
        }
    }
}