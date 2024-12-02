package digit.academy.tutorial.validators;

import java.util.Collections;

import org.apache.commons.lang3.ObjectUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import digit.academy.tutorial.repository.AdvocateRepository;
import digit.academy.tutorial.service.IndividualService;
import digit.academy.tutorial.web.models.Advocate;
import digit.academy.tutorial.web.models.AdvocateRequest;
import digit.academy.tutorial.web.models.AdvocateSearchCriteria;

@Component
public class AdvocateRegistrationValidator {

	private final AdvocateRepository advocateRepo;
	private final IndividualService individualService;

	@Autowired
	public AdvocateRegistrationValidator(AdvocateRepository advocateRepo, IndividualService individualService) {
		this.advocateRepo = advocateRepo;
		this.individualService = individualService;
	}

	public void validateAdvocateRegistration(AdvocateRequest advocateRequest) {
		RequestInfo requestInfo = advocateRequest.getRequestInfo();
		if (ObjectUtils.isEmpty(requestInfo) || requestInfo.getUserInfo() == null) {
			throw new CustomException("REQUEST_INFO_NOT_VALID", "Request info or User infor are not valid");
		}

		advocateRequest.getAdvocates().forEach(advocate -> {
			if (ObjectUtils.isEmpty(advocate.getTenantId())) {
				throw new CustomException("TENANT_ID_REQUIRED", "Tenant id is mandatory for advocate registration");
			}

			if (ObjectUtils.isEmpty(advocate.getIndividualId())) {
				throw new CustomException("INDIVIDUAL_ID_REQUIRED",
						"Individual id is mandatory for advocate registration");
			}

			// searching individual exist or not
			boolean isIndividualExist = individualService.isIndividualExist(
					Collections.singletonList(advocate.getIndividualId()), requestInfo, advocate.getTenantId());
			if (!isIndividualExist) {
				throw new CustomException("INDIVIDUAL_NOT_EXIST", "Individual does not exist");
			}
		});
	}

	public Advocate validateAdvocateExistence(Advocate advocate) {
		Advocate existingAdvocate = advocateRepo.getAdvocates(AdvocateSearchCriteria.builder().id(advocate.getId())
				.barRegistrationNumber(advocate.getBarRegistrationNumber())
				.applicationNumber(advocate.getApplicationNumber()).build()).stream().findFirst().orElse(null);

		if (existingAdvocate == null) {
			throw new CustomException("ADV_NOT_EXIST", "Advocate does not exist");
		}
		return existingAdvocate;
	}
}
