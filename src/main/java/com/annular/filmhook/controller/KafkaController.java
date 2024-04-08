package com.annular.filmhook.controller;

import com.annular.filmhook.service.kafka.KafkaMessageProducer;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @Autowired
    private KafkaMessageProducer messageProducer;

    @GetMapping("/sendKafkaMessage")
    public String sendMessage(@RequestParam("message") String message) throws JsonProcessingException, ExecutionException, InterruptedException {
        //messageProducer.sendMessage(message);
        //messageProducer.sendEvent(message);
        messageProducer.publishMessages(message);
        return "Message sent: " + message;
    }
}
