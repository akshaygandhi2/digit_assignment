package digit.academy.tutorial.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.egov.common.contract.models.Workflow;
import org.egov.common.contract.workflow.ProcessInstance;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.enrichment.AdvocateClerkEnrichment;
import digit.academy.tutorial.kafka.Producer;
import digit.academy.tutorial.repository.AdvocateClerkRepository;
import digit.academy.tutorial.util.WorkflowUtil;
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
	private final WorkflowUtil workflowUtil;
	private final SmsNotificationService smsNotificationService;

	public AdvocateClerkService(AdvocateClerkRegistrationValidator validator, AdvocateClerkEnrichment enrichment,
			Producer producer, Configuration configuration, AdvocateClerkRepository advocateClerkRepo,
			WorkflowUtil workflowUtil, SmsNotificationService smsNotificationService) {
		this.validator = validator;
		this.enrichment = enrichment;
		this.producer = producer;
		this.configuration = configuration;
		this.advocateClerkRepo = advocateClerkRepo;
		this.workflowUtil = workflowUtil;
		this.smsNotificationService = smsNotificationService;
	}

	/**
	 * Registers an advocate clerk based on the provided request.
	 * This method validates the registration, enriches the data, updates workflow status, 
	 * and pushes the registration to a Kafka topic for persistence.
	 *
	 * @param clerkRequest The request containing advocate clerk details and metadata.
	 * @return List of AdvocateClerk objects that were successfully registered.
	 */
	public List<AdvocateClerk> registerAdvocateClerkRequest(AdvocateClerkRequest clerkRequest) {
		try {
			// Validate registration
			validator.validateAdvocateClerkRegistration(clerkRequest);

			// Enrich registration
			enrichment.enrichAdvocateClerkRegistration(clerkRequest);

			// Update workflow status
			workflowUtil.updateAdvocateClerkWorkflowStatus(clerkRequest);

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

	/**
	 * Searches for advocate clerk registrations based on the provided search criteria.
	 * This method fetches advocate clerk details, retrieves their workflow status, and returns the list of clerks.
	 *
	 * @param searchRequest The request containing search criteria for advocate clerks.
	 * @return List of AdvocateClerk objects that match the search criteria.
	 */
	public List<AdvocateClerk> searchAdvocateClerkRegistration(AdvocateClerkSearchRequest searchRequest) {
		try {
			List<AdvocateClerk> clerks = searchRequest.getCriteria().stream()
					.map(advocateClerkRepo::getAdvocateClerkRegistrations).filter(Objects::nonNull)
					.flatMap(List::stream).toList();
			if (CollectionUtils.isEmpty(clerks)) {
				return Collections.emptyList();
			}

			List<String> applicationNumbers = clerks.stream().map(AdvocateClerk::getApplicationNumber).toList();

			List<ProcessInstance> processInstances = workflowUtil.getProcessInstances(searchRequest.getRequestInfo(),
					clerks.get(0).getTenantId(), applicationNumbers);

			clerks.forEach(clerk -> {
				Map<String, Workflow> workflowMap = workflowUtil.getWorkflow(processInstances);
				clerk.setWorkflow(workflowMap.get(clerk.getApplicationNumber()));
			});

			return clerks;
		} catch (CustomException e) {
			throw e;

		} catch (Exception e) {
			log.error("Error while fetching to search results : {}", e.toString());
			throw new CustomException("ADV_SEARCH_EX", e.getMessage());
		}
	}

	/**
	 * Updates the registration details for an advocate clerk.
	 * This method validates the clerk existence, updates the clerk details, enriches the data,
	 * updates the workflow status, and pushes the updated registration to a Kafka topic.
	 * If the clerk is activated, it sends a notification.
	 *
	 * @param clerkRequest The request containing advocate clerk details and metadata.
	 * @return The updated AdvocateClerk object.
	 */
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
			workflowUtil.updateAdvocateClerkWorkflowStatus(clerkRequest);

			producer.push(configuration.getAdvClerkUpdateTopic(), clerkRequest);

			// Call notification service if advocate is activated
			if (Boolean.TRUE.equals(clerk.getIsActive())) {
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
