package digit.academy.tutorial.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.enrichment.AdvocateClerkEnrichment;
import digit.academy.tutorial.kafka.Producer;
import digit.academy.tutorial.repository.AdvocateClerkRepository;
import digit.academy.tutorial.validators.AdvocateClerkRegistrationValidator;
import digit.academy.tutorial.web.models.AdvocateClerk;
import digit.academy.tutorial.web.models.AdvocateClerkRequest;
import digit.academy.tutorial.web.models.AdvocateClerkSearchRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdvocateClerkService {

	private final AdvocateClerkRegistrationValidator validator;
	private final AdvocateClerkEnrichment enrichment;
	private final Producer producer;
	private final Configuration configuration;
	private final AdvocateClerkRepository advocateClerkRepo;
	private final WorkflowService workflowService;
	private final SmsNotificationService smsNotificationService;

	@Autowired
	public AdvocateClerkService(AdvocateClerkRegistrationValidator validator, AdvocateClerkEnrichment enrichment,
			Producer producer, Configuration configuration, AdvocateClerkRepository advocateClerkRepo,
			WorkflowService workflowService, SmsNotificationService smsNotificationService) {
		this.validator = validator;
		this.enrichment = enrichment;
		this.producer = producer;
		this.configuration = configuration;
		this.advocateClerkRepo = advocateClerkRepo;
		this.workflowService = workflowService;
		this.smsNotificationService = smsNotificationService;
	}

	public List<AdvocateClerk> registerAdvocateClerkRequest(AdvocateClerkRequest clerkRequest) {
		try {
			// Validate registration
			validator.validateAdvocateClerkRegistration(clerkRequest);

			// Enrich registration
			enrichment.enrichAdvocateClerkRegistration(clerkRequest);

			// Update workflow status
			workflowService.updateAdvocateClerkWorkflowStatus(clerkRequest);

			// Push the registration to the topic for persister to listen and persist
			producer.push(configuration.getAdvClerkCreateTopic(), clerkRequest);
			log.info("Advocate clerk registration pushed successfully: " + clerkRequest.getClerks());
			return clerkRequest.getClerks();

		} catch (CustomException e) {
			throw e;

		} catch (Exception e) {
			log.error("Error while fetching to search results : {}", e.toString());
			throw new CustomException("ADV_CREATE_EX", e.getMessage());
		}
	}

	public List<AdvocateClerk> searchAdvocateClerkRegistration(AdvocateClerkSearchRequest searchRequest) {
		try {
			List<AdvocateClerk> clerks = searchRequest.getCriteria().stream().flatMap(criteria -> {
				List<AdvocateClerk> advocateClerks = advocateClerkRepo
						.getAdvocateClerkRegistrations(searchRequest.getCriteria().get(0));
				if (CollectionUtils.isEmpty(advocateClerks)) {
					return Stream.empty();
				}

				advocateClerks.forEach(clerk -> {
					clerk.setWorkflow(workflowService.getCurrentWorkflow(searchRequest.getRequestInfo(),
							clerk.getTenantId(), clerk.getApplicationNumber()));
				});
				return advocateClerks.stream();
			}).collect(Collectors.toList());

			return clerks;
		} catch (CustomException e) {
			throw e;

		} catch (Exception e) {
			log.error("Error while fetching to search results : {}", e.toString());
			throw new CustomException("ADV_SEARCH_EX", e.getMessage());
		}
	}

	public AdvocateClerk updateAdvocateClerkRegistration(AdvocateClerkRequest clerkRequest) {
		try {
			AdvocateClerk clerk = clerkRequest.getClerks().get(0);
			AdvocateClerk existingClerk = validator.validateAdvocateClerkExistence(clerk);
			existingClerk.setWorkflow(clerk.getWorkflow());
			existingClerk.setIsActive(clerk.getIsActive());
			existingClerk.setAdditionalDetails(clerk.getAdditionalDetails());

			clerkRequest.setClerks(Collections.singletonList(clerk));

			// Enrich registration update
			enrichment.enrichAdvocateClerkRegistrationUpdate(clerkRequest);

			// Update workflow status
			workflowService.updateAdvocateClerkWorkflowStatus(clerkRequest);

			producer.push(configuration.getAdvClerkUpdateTopic(), clerkRequest);

			// Call notification service if advocate is activated
			if (clerk.getIsActive()) {
				smsNotificationService.sendNotification(Collections.singletonList(clerk.getIndividualId()),
						clerkRequest.getRequestInfo(), clerk.getTenantId(), AdvocateClerk.class.getName(),
						clerk.getApplicationNumber());
			}
			return clerk;

		} catch (CustomException e) {
			throw e;

		} catch (Exception e) {
			log.error("Error while fetching to search results : {}", e.toString());
			throw new CustomException("ADV_UPDATE_EX", e.getMessage());
		}
	}
}
