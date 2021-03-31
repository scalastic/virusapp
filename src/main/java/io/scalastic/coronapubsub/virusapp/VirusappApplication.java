package io.scalastic.coronapubsub.virusapp;

import io.scalastic.coronapubsub.virusapp.model.Human;
import io.scalastic.coronapubsub.virusapp.pub.RedisHumanPublisher;
import io.scalastic.coronapubsub.virusapp.sub.RedisHumanSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VirusappApplication {

    @Autowired
    private RedisHumanPublisher humanPublisher;

    @Autowired
    private RedisHumanSubscriber humanSubscriber;

    public static void main(String[] args) {
        SpringApplication.run(VirusappApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's start!");

            Human john = new Human(1L, "John", false, false);
            humanPublisher.publish(john);

            System.out.println("End!!");
        };
    }
}
