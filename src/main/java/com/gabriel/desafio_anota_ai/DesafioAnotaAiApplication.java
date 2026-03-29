package com.gabriel.desafio_anota_ai;

import io.github.cdimascio.dotenv.Dotenv;
import com.gabriel.desafio_anota_ai.config.aws.AwsSnsTopicProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@EnableConfigurationProperties(AwsSnsTopicProperties.class)
public class DesafioAnotaAiApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry -> {
			if (System.getenv(entry.getKey()) == null) {
				System.setProperty(entry.getKey(), entry.getValue());
			}
		});
		SpringApplication.run(DesafioAnotaAiApplication.class, args);
	}

}
