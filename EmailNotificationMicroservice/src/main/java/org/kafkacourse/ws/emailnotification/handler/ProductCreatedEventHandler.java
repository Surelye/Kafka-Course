package org.kafkacourse.ws.emailnotification.handler;

import org.kafkacourse.ws.core.ProductCreatedEvent;
import org.kafkacourse.ws.emailnotification.exception.NonRetryableException;
import org.kafkacourse.ws.emailnotification.exception.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {

    private final RestTemplate restTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(ProductCreatedEventHandler.class);

    @Autowired
    public ProductCreatedEventHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @KafkaHandler
    public void handle(ProductCreatedEvent productCreatedEvent) {
        LOGGER.info("Received event: {}, productId: {}", productCreatedEvent.getTitle(),
                productCreatedEvent.getProductId());

        String url = "http://localhost:8090/response/200";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                LOGGER.info("Received response: {}", response.getBody());
            }
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage());
            throw new RetryableException(e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        }
    }
}
