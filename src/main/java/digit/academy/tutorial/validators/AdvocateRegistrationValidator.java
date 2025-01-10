package digit.academy.tutorial.validators;

import static digit.academy.tutorial.config.ServiceConstants.*;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
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

	public AdvocateRegistrationValidator(AdvocateRepository advocateRepo, IndividualService individualService) {
		this.advocateRepo = advocateRepo;
		this.individualService = individualService;
	}

	/**
	 * Validates the advocate registration request by checking mandatory fields and
	 * verifying the existence of individuals associated with the advocates.
	 *
	 * @param advocateRequest the request containing advocate details and request
	 *                        info
	 * @throws CustomException if request info is invalid, tenant ID is missing,
	 *                         individual ID is missing, or the individual does not
	 *                         exist
	 */
	public void validateAdvocateRegistration(AdvocateRequest advocateRequest) {
		RequestInfo requestInfo = advocateRequest.getRequestInfo();
		if (ObjectUtils.isEmpty(requestInfo) || requestInfo.getUserInfo() == null) {
			throw new CustomException(REQUEST_INFO_NOT_VALID, REQUEST_INFO_NOT_VALID_ERROR);
		}

		List<Advocate> advocates = advocateRequest.getAdvocates();
		advocates.forEach(advocate -> {
			if (ObjectUtils.isEmpty(advocate.getTenantId())) {
				throw new CustomException(TENANT_ID_REQUIRED, TENANT_ID_REQUIRED_ERROR);
			}

			if (ObjectUtils.isEmpty(advocate.getIndividualId())) {
				throw new CustomException(INDIVIDUAL_ID_REQUIRED, INDIVIDUAL_ID_REQUIRED_ERROR);
			}
		});

		List<String> individualIds = advocates.stream().map(Advocate::getIndividualId).filter(Objects::nonNull)
				.toList();

		// searching individual exist or not
		boolean isIndividualExist = individualService.isIndividualExist(individualIds, requestInfo,
				advocates.get(0).getTenantId());
		if (!isIndividualExist) {
			throw new CustomException(INDIVIDUAL_NOT_EXIST, INDIVIDUAL_NOT_EXIST_ERROR);
		}
	}

	/**
	 * Validates whether an advocate already exists in the system based on the given
	 * criteria.
	 *
	 * @param advocate the advocate whose existence needs to be validated
	 * @return the existing advocate if found
	 * @throws CustomException if the advocate does not exist
	 */
	public Advocate validateAdvocateExistence(Advocate advocate) {
		Advocate existingAdvocate = advocateRepo.getAdvocates(AdvocateSearchCriteria.builder().id(advocate.getId())
				.barRegistrationNumber(advocate.getBarRegistrationNumber())
				.applicationNumber(advocate.getApplicationNumber()).build()).stream().findFirst().orElse(null);

		if (existingAdvocate == null) {
			throw new CustomException(ADV_NOT_EXIST, ADV_NOT_EXIST_ERROR);
		}
		return existingAdvocate;
	}
}
