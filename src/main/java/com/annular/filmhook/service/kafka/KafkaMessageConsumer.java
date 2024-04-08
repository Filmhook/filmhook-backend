package com.annular.filmhook.service.kafka;

import com.annular.filmhook.configuration.KafkaConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageConsumer implements Runnable {

    public static final Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    @Autowired
    KafkaConfig kafkaConfig;

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        logger.info("Received message: {}", message);
    }

//	@Override
//	public void run() {
//		kafkaConfig.kafkaConsumer().subscribe(Arrays.asList(kafkaConfig.getTopic()));
//        while (true) {
//        	kafkaConfig.kafkaConsumer().poll(Duration.ofMillis(1000))
//        	.forEach(record -> logger.info("Received message :- " + record)); 
//        } 
//    }

    @Override
    public void run() {
    }
}
