package com.annular.filmhook.service.kafka;

import com.annular.filmhook.configuration.KafkaConfig;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageProducer {

    public static final Logger logger = LoggerFactory.getLogger(KafkaMessageProducer.class);

    @Autowired
    KafkaConfig kafkaConfig;

//    public void sendMessage(String message) {
//        kafkaProducerConfig.kafkaTemplate().send(kafkaProperties.getTopic(), message);
//    }
//
//    public CompletableFuture<?> sendEvent(String msg) throws JsonProcessingException {
//        String key = "1";
//        CompletableFuture<?> completableFuture = kafkaProducerConfig.kafkaTemplate().send(kafkaProperties.getTopic(), key, msg);
//        return completableFuture.whenComplete(((sendResult, throwable) -> {
//            if (throwable != null) {
//                handleFailure(key, msg, throwable);
//            } else {
//                handleSuccess(key, msg, sendResult);
//            }
//        }));
//        // Another way
//        /*List<Header> recordHeader = List.of(new RecordHeader("event-source", "inventory-event-producer".getBytes()));
//        var producerRecord = new ProducerRecord<>(topic, null, key, value, recordHeader);
//        kafkaTemplate.send(producerRecord);*/
//    }

    private void handleSuccess(String key, String value, Object sendResult) {
        logger.info("Message sent successfully for the key: {} and the value: {}, partition is: {}", key, value,
                ((SendResult<?, ?>) sendResult).getRecordMetadata().partition());
    }

    private void handleFailure(String key, String value, Throwable throwable) {
        logger.error("Error sending message and exception is {}", throwable.getMessage(), throwable);
    }

    public void publishMessages(String message) throws ExecutionException, InterruptedException {
        try (KafkaProducer<String, String> producer = kafkaConfig.kafkaProducer()) {
            kafkaConfig.createTopicIfNotExist(); // Checking the topic existence. creating it if not exist.
            producer.send(new ProducerRecord<String, String>(kafkaConfig.getTopic(), message));
        }
    }

    public void publishMessageAsync(String message) throws ExecutionException, InterruptedException {
        try (KafkaProducer<String, String> producer = kafkaConfig.kafkaProducer()) {
            String key = "1";
            kafkaConfig.createTopicIfNotExist(); // Checking the topic existence. creating it if not exist.
            CompletableFuture<?> completableFuture = (CompletableFuture<?>) producer.send(new ProducerRecord<String, String>(kafkaConfig.getTopic(), message));
            completableFuture.whenComplete(
                    (sendResult, throwable) -> {
                        if (throwable != null) {
                            handleFailure(key, message, throwable);
                        } else {
                            handleSuccess(key, message, sendResult);
                        }
                    }
            );
        }
    }

}
