package digit.academy.tutorial.validators;

import static digit.academy.tutorial.config.ServiceConstants.*;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

import digit.academy.tutorial.repository.AdvocateClerkRepositoryImpl;
import digit.academy.tutorial.service.IndividualService;
import digit.academy.tutorial.web.models.AdvocateClerk;
import digit.academy.tutorial.web.models.AdvocateClerkRequest;
import digit.academy.tutorial.web.models.AdvocateClerkSearchCriteria;

@Component
public class AdvocateClerkRegistrationValidator {

	private final AdvocateClerkRepositoryImpl advocateClerkRepo;
	private final IndividualService individualService;

	public AdvocateClerkRegistrationValidator(AdvocateClerkRepositoryImpl advocateClerkRepo,
			IndividualService individualService) {
		this.advocateClerkRepo = advocateClerkRepo;
		this.individualService = individualService;
	}

	/**
	 * Validates the registration details of advocate clerks. Ensures tenant and
	 * individual IDs are provided and verifies their existence.
	 *
	 * @param advocateClerkRequest The request object containing clerks' details to
	 *                             validate.
	 */
	public void validateAdvocateClerkRegistration(AdvocateClerkRequest advocateClerkRequest) {
		RequestInfo requestInfo = advocateClerkRequest.getRequestInfo();
		if (ObjectUtils.isEmpty(requestInfo) || ObjectUtils.isEmpty(requestInfo.getUserInfo())) {
			throw new CustomException(REQUEST_INFO_NOT_VALID, REQUEST_INFO_NOT_VALID_ERROR);
		}

		List<AdvocateClerk> clerks = advocateClerkRequest.getClerks();
		clerks.forEach(advocateClerk -> {
			if (ObjectUtils.isEmpty(advocateClerk.getTenantId())) {
				throw new CustomException(TENANT_ID_REQUIRED, TENANT_ID_REQUIRED_ERROR);
			}

			if (ObjectUtils.isEmpty(advocateClerk.getIndividualId())) {
				throw new CustomException(INDIVIDUAL_ID_REQUIRED, INDIVIDUAL_ID_REQUIRED_ERROR);
			}
		});

		List<String> individualIds = clerks.stream().map(AdvocateClerk::getIndividualId).filter(Objects::nonNull)
				.toList();

		// searching individual exist or not
		boolean isIndividualExist = individualService.isIndividualExist(individualIds, requestInfo,
				clerks.get(0).getTenantId());
		if (!isIndividualExist) {
			throw new CustomException(INDIVIDUAL_NOT_EXIST, INDIVIDUAL_NOT_EXIST_ERROR);
		}
	}

	/**
	 * Validates the existence of an advocate clerk based on provided criteria.
	 *
	 * @param clerk The clerk whose existence is to be validated.
	 * @return The existing AdvocateClerk object if found.
	 * @throws CustomException If the clerk does not exist.
	 */
	public AdvocateClerk validateAdvocateClerkExistence(AdvocateClerk clerk) {
		AdvocateClerk existingClerk = advocateClerkRepo.getAdvocateClerkRegistrations(
				AdvocateClerkSearchCriteria.builder().id(clerk.getId()).applicationNumber(clerk.getApplicationNumber())
						.stateRegnNumber(clerk.getStateRegnNumber()).build())
				.stream().findFirst().orElse(null);

		if (existingClerk == null) {
			throw new CustomException(CLERK_NOT_EXIST, CLERK_NOT_EXIST_ERROR);
		}
		return existingClerk;
	}
}
