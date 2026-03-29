package com.gabriel.desafio_anota_ai.config.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.sns.topic")
public record AwsSnsTopicProperties(String catalogArn) {
}
