package com.gabriel.desafio_anota_ai.services.aws;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.Topic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AwsSnsService {

    AmazonSNS snsClient;

    Topic catalogTopicArn;

    public AwsSnsService(AmazonSNS snsClient, @Qualifier("catalogEventsTopic")Topic catalogTopicArn) {
        this.snsClient = snsClient;
        this.catalogTopicArn = catalogTopicArn;
    }

    public void publish(MessageDTO message) {
        this.snsClient.publish(catalogTopicArn.getTopicArn(), message.toString());
    }
}
