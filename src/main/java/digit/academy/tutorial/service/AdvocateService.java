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
import digit.academy.tutorial.enrichment.AdvocateEnrichment;
import digit.academy.tutorial.kafka.Producer;
import digit.academy.tutorial.repository.AdvocateRepository;
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
	private final WorkflowService workflowService;
	private final SmsNotificationService smsNotificationService;

	@Autowired
	public AdvocateService(AdvocateRegistrationValidator validator, AdvocateEnrichment enrichment, Producer producer,
			Configuration configuration, AdvocateRepository advocateRepo, WorkflowService workflowService,
			SmsNotificationService smsNotificationService) {
		this.validator = validator;
		this.enrichment = enrichment;
		this.producer = producer;
		this.configuration = configuration;
		this.advocateRepo = advocateRepo;
		this.workflowService = workflowService;
		this.smsNotificationService = smsNotificationService;
	}

	public List<Advocate> registerAdvocateRequest(AdvocateRequest advocateRequest) {
		try {
			// Validate registration
			validator.validateAdvocateRegistration(advocateRequest);

			// Enrich registration
			enrichment.enrichAdvocateRegistration(advocateRequest);

			// Update workflow status
			workflowService.updateAdvocateWorkflowStatus(advocateRequest);

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

	public List<Advocate> searchAdvocateRegistration(AdvocateSearchRequest searchRequest) {
		try {
			List<Advocate> advocateList = searchRequest.getCriteria().stream().flatMap(criteria -> {
				List<Advocate> advocates = advocateRepo.getAdvocates(criteria);
				if (CollectionUtils.isEmpty(advocates)) {
					return Stream.empty();
				}

				advocates.forEach(advocate -> {
					advocate.setWorkflow(workflowService.getCurrentWorkflow(searchRequest.getRequestInfo(),
							advocate.getTenantId(), advocate.getApplicationNumber()));
				});
				return advocates.stream();
			}).collect(Collectors.toList());

			return advocateList;
		} catch (CustomException e) {
			throw e;

		} catch (Exception e) {
			log.error("Error while fetching to search results : {}", e.toString());
			throw new CustomException("ADV_SEARCH_EX", e.getMessage());
		}
	}

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
			workflowService.updateAdvocateWorkflowStatus(advocateRequest);

			producer.push(configuration.getAdvUpdateTopic(), advocateRequest);
			log.info("Advocate registration updation pushed successfully: " + advocateRequest.getAdvocates());

			// Call notification service if advocate is activated
			if (advocate.getIsActive()) {
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
