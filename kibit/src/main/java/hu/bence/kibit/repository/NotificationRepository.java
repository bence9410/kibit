package hu.bence.kibit.repository;

import hu.bence.kibit.entity.Transaction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationRepository {

    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    public NotificationRepository(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Transaction transaction) {
        kafkaTemplate.send("notification", transaction);
    }

}
