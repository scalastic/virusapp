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

import java.util.Arrays;

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

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}

			System.out.println("Starts publishing...");

			Human john = new Human(1L,"John", false, false);
			humanPublisher.publish(john);
		};
	}
}
