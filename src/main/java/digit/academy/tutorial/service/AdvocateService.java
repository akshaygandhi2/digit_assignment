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
import digit.academy.tutorial.enrichment.AdvocateEnrichment;
import digit.academy.tutorial.kafka.Producer;
import digit.academy.tutorial.repository.AdvocateRepository;
import digit.academy.tutorial.util.WorkflowUtil;
import digit.academy.tutorial.validators.AdvocateRegistrationValidator;
import digit.academy.tutorial.web.models.Advocate;
import digit.academy.tutorial.web.models.AdvocateRequest;
import digit.academy.tutorial.web.models.AdvocateSearchRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdvocateService {

	private final AdvocateRegistrationValidator validator;
	private final AdvocateEnrichment enrichment;
	private final Producer producer;
	private final Configuration configuration;
	private final AdvocateRepository advocateRepo;
	private final WorkflowUtil workflowUtil;
	private final SmsNotificationService smsNotificationService;

	public AdvocateService(AdvocateRegistrationValidator validator, AdvocateEnrichment enrichment, Producer producer,
			Configuration configuration, AdvocateRepository advocateRepo, WorkflowUtil workflowUtil,
			SmsNotificationService smsNotificationService) {
		this.validator = validator;
		this.enrichment = enrichment;
		this.producer = producer;
		this.configuration = configuration;
		this.advocateRepo = advocateRepo;
		this.workflowUtil = workflowUtil;
		this.smsNotificationService = smsNotificationService;
	}

	/**
	 * Handles the registration of advocates. 1. Validates the input request. 2.
	 * Enriches the request data for processing. 3. Updates workflow status. 4.
	 * Publishes the registration request to Kafka for persistence.
	 *
	 * @param advocateRequest The advocate registration request object.
	 * @return List of registered advocates.
	 */
	public List<Advocate> registerAdvocateRequest(AdvocateRequest advocateRequest) {
		try {
			// Validate registration
			validator.validateAdvocateRegistration(advocateRequest);

			// Enrich registration
			enrichment.enrichAdvocateRegistration(advocateRequest);

			// Update workflow status
			workflowUtil.updateAdvocateWorkflowStatus(advocateRequest);

			// Push the registration to the topic for persister to listen and persist
			producer.push(configuration.getAdvCreateTopic(), advocateRequest);
			log.info("Advocate registration pushed successfully: " + advocateRequest.getAdvocates());
			return advocateRequest.getAdvocates();

		} catch (CustomException e) {
			throw e;

		} catch (Exception e) {
			log.error("Error occurred while creating advocate :: {}", e.toString());
			throw new CustomException("ADV_CREATE_EX", e.getMessage());
		}
	}

	/**
	 * Searches for advocate registrations based on criteria. 1. Retrieves advocates
	 * from the repository matching the criteria. 2. Fetches workflow process
	 * instances for the retrieved advocates. 3. Maps the workflow details back to
	 * the corresponding advocates.
	 *
	 * @param searchRequest The advocate search request object containing search
	 *                      criteria.
	 * @return List of advocates matching the search criteria.
	 */
	public List<Advocate> searchAdvocateRegistration(AdvocateSearchRequest searchRequest) {
		try {
			List<Advocate> advocateList = searchRequest.getCriteria().stream().map(advocateRepo::getAdvocates)
					.filter(Objects::nonNull).flatMap(List::stream).toList();
			if (CollectionUtils.isEmpty(advocateList)) {
				return Collections.emptyList();
			}

			List<String> applicationNumbers = advocateList.stream().map(Advocate::getApplicationNumber).toList();

			List<ProcessInstance> processInstances = workflowUtil.getProcessInstances(searchRequest.getRequestInfo(),
					advocateList.get(0).getTenantId(), applicationNumbers);

			advocateList.forEach(advocate -> {
				Map<String, Workflow> workflowMap = workflowUtil.getWorkflow(processInstances);
				advocate.setWorkflow(workflowMap.get(advocate.getApplicationNumber()));
			});

			return advocateList;
		} catch (CustomException e) {
			throw e;

		} catch (Exception e) {
			log.error("Error while fetching to search results : {}", e.toString());
			throw new CustomException("ADV_SEARCH_EX", e.getMessage());
		}
	}

	/**
	 * Updates the registration details of an advocate. 1. Validates the existence
	 * of the advocate. 2. Updates the existing advocate details. 3. Enriches the
	 * updated advocate data. 4. Updates workflow status. 5. Publishes the update
	 * request to Kafka for persistence. 6. Sends SMS notifications if the advocate
	 * is activated.
	 *
	 * @param advocateRequest The advocate update request object.
	 * @return The updated advocate object.
	 */
	public Advocate updateAdvocateRegistration(AdvocateRequest advocateRequest) {
		try {
			Advocate advocate = advocateRequest.getAdvocates().get(0);
			Advocate existingAdvocate = validator.validateAdvocateExistence(advocate);
			existingAdvocate.setWorkflow(advocate.getWorkflow());
			existingAdvocate.setIsActive(advocate.getIsActive());
			existingAdvocate.setAdditionalDetails(advocate.getAdditionalDetails());

			log.info(existingAdvocate.toString());

			advocateRequest.setAdvocates(Collections.singletonList(existingAdvocate));

			// Enrich registration update
			enrichment.enrichAdvocateRegistrationUpdate(advocateRequest);

			// Update workflow status
			workflowUtil.updateAdvocateWorkflowStatus(advocateRequest);

			producer.push(configuration.getAdvUpdateTopic(), advocateRequest);
			log.info("Advocate registration updation pushed successfully: " + advocateRequest.getAdvocates());

			// Call notification service if advocate is activated
			if (Boolean.TRUE.equals(advocate.getIsActive())) {
				smsNotificationService.sendNotification(Collections.singletonList(advocate.getIndividualId()),
						advocateRequest.getRequestInfo(), advocate.getTenantId(), Advocate.class.getName(),
						advocate.getApplicationNumber());
			}
			return advocate;

		} catch (CustomException e) {
			throw e;

		} catch (Exception e) {
			log.error("Error occurred while updating advocate :: {}", e.toString());
			throw new CustomException("ADV_UPDATE_EX", "Error occurred while updating advocate: " + e.getMessage());
		}
	}
}
