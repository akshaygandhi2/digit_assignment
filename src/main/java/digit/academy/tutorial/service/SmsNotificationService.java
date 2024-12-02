package digit.academy.tutorial.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.models.core.SMSRequest;
import org.egov.common.models.individual.Individual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.kafka.Producer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SmsNotificationService {

	private static final String smsTemplate = "Dear sir/madam, your {APPTYPE} registration application has been successfully APPROVED with your application number - {APPNUM}.";

	private final Configuration config;
	private final Producer producer;
	private final IndividualService individualService;

	@Autowired
	public SmsNotificationService(Configuration config, Producer producer, IndividualService individualService) {
		this.config = config;
		this.producer = producer;
		this.individualService = individualService;
	}

	public void sendNotification(List<String> individualIds, RequestInfo requestInfo, String tenantId, String appType,
			String applicationNumber) {
		// Get the individuals from individual service
		List<Individual> individuals = individualService.searchIndividuals(individualIds, requestInfo, tenantId);

		String message = smsTemplate.replace("{APPTYPE}", appType).replace("{APPNUM}", applicationNumber);

		individuals.forEach(individual -> {
			SMSRequest smsRequest = SMSRequest.builder().mobileNumber(individual.getMobileNumber()).message(message)
					.build();
			producer.push(config.getSmsNotificationTopic(), smsRequest);
			log.info("Message pused successfully: ", individual.getMobileNumber());
		});
	}
}
