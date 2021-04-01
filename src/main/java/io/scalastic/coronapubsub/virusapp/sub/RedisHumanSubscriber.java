package io.scalastic.coronapubsub.virusapp.sub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.scalastic.coronapubsub.virusapp.model.Human;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

@Service
public class RedisHumanSubscriber implements MessageListener {

    ObjectMapper objectMapper = new ObjectMapper();

    StringRedisSerializer serializer = new StringRedisSerializer();

    @Override
    public void onMessage(Message message, byte[] bytes) {
        Human human = null;
        try {
            human = objectMapper.readValue(message.toString(), Human.class);
            human.setIsInfected(!human.getIsVaccinated());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("Subscribed " + message.toString());
        System.out.println("Transformed " + human.toString());
    }
}
