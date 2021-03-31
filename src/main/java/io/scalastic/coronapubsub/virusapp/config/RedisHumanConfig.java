package io.scalastic.coronapubsub.virusapp.config;

import io.scalastic.coronapubsub.virusapp.model.Human;
import io.scalastic.coronapubsub.virusapp.pub.RedisHumanPublisher;
import io.scalastic.coronapubsub.virusapp.sub.RedisHumanSubscriber;
import org.apache.commons.pool2.impl.DefaultEvictionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.nativex.hint.TypeHint;

import javax.annotation.Resource;
import java.util.concurrent.Executors;

@Configuration
@PropertySource(name = "application", value = "classpath:application.properties")
@ComponentScan("io.scalastic.coronapubsub.virusapp")
@TypeHint(types = {GenericToStringSerializer.class, Human.class, DefaultEvictionPolicy.class}, typeNames = {"redis.clients.jedis.Queable", "redis.clients.jedis.Builder"})
public class RedisHumanConfig {

    @Resource
    ConfigurableEnvironment environment;

    @Bean
    public PropertiesPropertySource propertySource() {
        return (PropertiesPropertySource) environment.getPropertySources().get("application");
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory redis = new JedisConnectionFactory();
        redis.setHostName(environment.getProperty("spring.redis.host"));
        redis.setPort(environment.getProperty("spring.redis.port", Integer.class));
        redis.setUsePool(true);
        redis.afterPropertiesSet();
        return redis;
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
        container.setTaskExecutor(Executors.newCachedThreadPool());
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
