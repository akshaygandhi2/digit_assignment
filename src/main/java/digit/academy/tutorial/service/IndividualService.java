package digit.academy.tutorial.service;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.academy.tutorial.util.IndividualUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IndividualService {

	private final IndividualUtil individualUtil;
	private final ObjectMapper mapper;

	public IndividualService(IndividualUtil individualUtil, ObjectMapper mapper) {
		this.individualUtil = individualUtil;
		this.mapper = mapper;
	}

	/**
	 * Checks if an individual exists based on the provided list of IDs. This method
	 * searches for the individuals by their IDs and returns true if any individual
	 * exists, otherwise false.
	 *
	 * @param ids         The list of individual IDs to be checked.
	 * @param requestInfo Information about the request (such as user and request
	 *                    context).
	 * @param tenantId    The tenant ID associated with the individuals.
	 * @return true if individuals are found, false otherwise.
	 */
	public boolean isIndividualExist(List<String> ids, RequestInfo requestInfo, String tenantId) {
		List<Individual> individuals = searchIndividuals(ids, requestInfo, tenantId);
		return !CollectionUtils.isEmpty(individuals) && !individuals.isEmpty();
	}

	/**
	 * Searches for individuals based on the provided list of IDs. This method
	 * interacts with an external service to fetch individual details and maps the
	 * response into a list of Individual objects.
	 *
	 * @param ids         The list of individual IDs to search for.
	 * @param requestInfo Information about the request (such as user and request
	 *                    context).
	 * @param tenantId    The tenant ID associated with the individuals.
	 * @return A list of Individual objects corresponding to the provided IDs.
	 */
	public List<Individual> searchIndividuals(List<String> ids, RequestInfo requestInfo, String tenantId) {
		Object response = individualUtil.fetchIndividualDetails(ids, requestInfo, tenantId);
		if (ObjectUtils.isEmpty(response)) {
			return Collections.emptyList();
		}
		IndividualBulkResponse individualResponse = mapper.convertValue(response, IndividualBulkResponse.class);
		return individualResponse.getIndividual();
	}
}
