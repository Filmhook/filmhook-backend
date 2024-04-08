package com.annular.filmhook.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import lombok.Data;

@Configuration
@Data
public class KafkaConfig {

    public static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String commonBootstrapServers;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String producerBootstrapServers;

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String consumerBootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.producer.value-serializer}")
    private String serializerClass;

    @Value("${spring.kafka.producer.properties.partitions}")
    private Integer partitions;

    @Value("${spring.kafka.producer.properties.replication}")
    private Short replication;

    @Value("${spring.kafka.producer.properties.retention}")
    private String retention;

    @Autowired
    private KafkaAdmin kafkaAdmin;

    public void createTopicIfNotExist() {
        try (AdminClient admin = AdminClient.create(getProperties())) {
            ListTopicsResult listTopics = admin.listTopics();
            Set<String> existingTopics = listTopics.names().get();
            if (!existingTopics.contains(topic)) {
                admin.createTopics(getNewTopicList());
            }
        } catch (Exception e) {
            logger.error("Error at createTopicIfNotExist()...", e);
        }
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", getCommonBootstrapServers());
        properties.put("key.serializer", getSerializerClass());
        properties.put("value.serializer", getSerializerClass());
        return properties;
    }

    public List<NewTopic> getNewTopicList() {
        List<NewTopic> topicList = new ArrayList<>();
        Map<String, String> configs = new HashMap<>();
        configs.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);
        configs.put(TopicConfig.RETENTION_MS_CONFIG, getRetention());
        NewTopic newTopic = new NewTopic(getTopic(), getPartitions(), getReplication()).configs(configs);
        topicList.add(newTopic);
        return topicList;
    }


    public Properties getProducerProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getProducerBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    public Properties getConsumerProperties() {
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getConsumerBootstrapServers());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return props;
    }

    public KafkaProducer<String, String> kafkaProducer() {
        return new KafkaProducer<>(this.getProducerProperties());
    }

    public KafkaConsumer<String, String> kafkaConsumer() {
        return new KafkaConsumer<>(this.getConsumerProperties());
    }

}
