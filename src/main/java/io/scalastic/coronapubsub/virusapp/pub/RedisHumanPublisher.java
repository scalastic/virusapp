package io.scalastic.coronapubsub.virusapp.pub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.scalastic.coronapubsub.virusapp.model.Human;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

public class RedisHumanPublisher {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ChannelTopic topic;

    public RedisHumanPublisher() {
    }

    public RedisHumanPublisher(RedisTemplate redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(Human human) {

        try {
            redisTemplate.convertAndSend(topic.getTopic(), objectMapper.writeValueAsString(human));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("Published " + human);
    }
}
