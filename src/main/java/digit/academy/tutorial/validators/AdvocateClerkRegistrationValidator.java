package digit.academy.tutorial.validators;

import java.util.Collections;

import org.apache.commons.lang3.ObjectUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import digit.academy.tutorial.repository.AdvocateClerkRepository;
import digit.academy.tutorial.service.IndividualService;
import digit.academy.tutorial.web.models.AdvocateClerk;
import digit.academy.tutorial.web.models.AdvocateClerkRequest;
import digit.academy.tutorial.web.models.AdvocateClerkSearchCriteria;

@Component
public class AdvocateClerkRegistrationValidator {

	private final AdvocateClerkRepository advocateClerkRepo;
	private final IndividualService individualService;

	@Autowired
	public AdvocateClerkRegistrationValidator(AdvocateClerkRepository advocateClerkRepo,
			IndividualService individualService) {
		this.advocateClerkRepo = advocateClerkRepo;
		this.individualService = individualService;
	}

	public void validateAdvocateClerkRegistration(AdvocateClerkRequest advocateClerkRequest) {
		RequestInfo requestInfo = advocateClerkRequest.getRequestInfo();
		if (ObjectUtils.isEmpty(requestInfo) || requestInfo.getUserInfo() == null) {
			throw new CustomException("REQUEST_INFO_NOT_VALID", "Request info or User infor are not valid");
		}

		advocateClerkRequest.getClerks().forEach(advocateClerk -> {
			if (ObjectUtils.isEmpty(advocateClerk.getTenantId())) {
				throw new CustomException("TENANT_ID_REQUIRED",
						"Tenant id is mandatory for advocate clerk registration.");
			}

			if (ObjectUtils.isEmpty(advocateClerk.getIndividualId())) {
				throw new CustomException("INDIVIDUAL_ID_REQUIRED",
						"Individual id is mandatory for advocate clerk registration.");
			}

			// searching individual exist or not
			boolean isIndividualExist = individualService.isIndividualExist(
					Collections.singletonList(advocateClerk.getIndividualId()), requestInfo,
					advocateClerk.getTenantId());
			if (!isIndividualExist) {
				throw new CustomException("INDIVIDUAL_NOT_EXIST", "Individual does not exist");
			}
		});
	}

	public AdvocateClerk validateAdvocateClerkExistence(AdvocateClerk clerk) {
		AdvocateClerk existingClerk = advocateClerkRepo.getAdvocateClerkRegistrations(
				AdvocateClerkSearchCriteria.builder().id(clerk.getId()).applicationNumber(clerk.getApplicationNumber())
						.stateRegnNumber(clerk.getStateRegnNumber()).build())
				.stream().findFirst().orElse(null);

		if (existingClerk == null) {
			throw new CustomException("CLERK_NOT_EXIST", "Advocate clerk does not exist");
		}
		return existingClerk;
	}
}
