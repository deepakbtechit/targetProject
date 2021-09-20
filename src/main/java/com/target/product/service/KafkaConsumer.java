package com.target.product.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class KafkaConsumer {
    @KafkaListener(topics = "target-events", groupId = "group_id")
    public void consume(String message) throws IOException {
        log.info(String.format("Kafka consumer -> %s", message));
    }
}
