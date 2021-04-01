package io.scalastic.coronapubsub.virusapp.config;

import io.scalastic.coronapubsub.virusapp.model.Human;
import io.scalastic.coronapubsub.virusapp.pub.RedisHumanPublisher;
import io.scalastic.coronapubsub.virusapp.sub.RedisHumanSubscriber;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
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
@TypeHint(types = {Human.class})
public class RedisHumanConfig {

    @Resource
    ConfigurableEnvironment environment;

    @Bean
    public PropertiesPropertySource propertySource() {
        return (PropertiesPropertySource) environment.getPropertySources().get("application");
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {

        LettuceConnectionFactory lettuceconnectionFactory = new LettuceConnectionFactory(
                new RedisStandaloneConfiguration(
                        environment.getProperty("spring.redis.host"),
                        environment.getProperty("spring.redis.port", Integer.class)));
        lettuceconnectionFactory.afterPropertiesSet();
        return lettuceconnectionFactory;
    }

    @Bean
    public RedisTemplate redisTemplate() {
        final RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(lettuceConnectionFactory());
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
        container.setConnectionFactory(lettuceConnectionFactory());
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
