package digit.academy.tutorial.kafka;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class Consumer {

	/*
	 * Uncomment the below line to start consuming record from kafka.topics.consumer
	 * Value of the variable kafka.topics.consumer should be overwritten in
	 * application.properties
	 */
	// @KafkaListener(topics = {"kafka.topics.consumer"})
	public void listen(final Map<String, Object> recordMap) {
		// TODO document why this method is empty
	}
}
