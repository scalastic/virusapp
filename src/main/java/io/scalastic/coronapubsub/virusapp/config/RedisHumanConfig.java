package io.scalastic.coronapubsub.virusapp.config;

import io.scalastic.coronapubsub.virusapp.pub.RedisHumanPublisher;
import io.scalastic.coronapubsub.virusapp.sub.RedisHumanSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import java.util.concurrent.Executors;

@Configuration
@ComponentScan("io.scalastic.coronapubsub.virusapp")
public class RedisHumanConfig {

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate redisTemplate() {
        final RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer(Object.class));
        return template;
    }

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new RedisHumanSubscriber());
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(messageListener(), topic());
        //container.setTaskExecutor(Executors.newFixedThreadPool(4));
        return container;
    }

    @Bean
    RedisHumanPublisher redisPublisher() {
        return new RedisHumanPublisher(redisTemplate(), topic());
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic("human");
    }

}
