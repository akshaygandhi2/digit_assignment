package digit.academy.tutorial.util;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.models.individual.IndividualSearch;
import org.egov.common.models.individual.IndividualSearchRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.repository.ServiceRequestRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IndividualUtil {

	private final Configuration config;
	private final ServiceRequestRepository requestRepo;

	public IndividualUtil(Configuration config, ServiceRequestRepository requestRepo) {
		this.config = config;
		this.requestRepo = requestRepo;
	}

	/**
     * This method fetches individual details from the individual service by making a request.
     * It builds the search URL with parameters and sends the request to fetch individual data.
     *
     * @param ids        A list of individual IDs whose details need to be fetched
     * @param requestInfo Information related to the request (e.g., authentication)
     * @param tenantId   The tenant ID for which the data needs to be fetched
     * @return The response containing the individual details fetched from the service
     */
	public Object fetchIndividualDetails(List<String> ids, RequestInfo requestInfo, String tenantId) {
		String uri = getSearchURLWithParams(tenantId).toUriString();

		IndividualSearch individualSearch = IndividualSearch.builder().id(ids).build();
		IndividualSearchRequest individualSearchRequest = IndividualSearchRequest.builder().requestInfo(requestInfo)
				.individual(individualSearch).build();

		log.info("IndividualUtil::fetchIndividualDetails::call individual search with tenantId::" + tenantId
				+ "::individual ids::" + ids);

		return requestRepo.fetchResult(new StringBuilder(uri), individualSearchRequest);
	}

	/**
     * This method constructs the search URL with the necessary query parameters.
     * 
     * @param tenantId The tenant ID that is used as a query parameter
     * @return The UriComponentsBuilder with the complete search URL and parameters
     */
	private UriComponentsBuilder getSearchURLWithParams(String tenantId) {
		StringBuilder uri = new StringBuilder();
		uri.append(config.getIndividualHost()).append(config.getIndividualSearchEndpoint());
		return UriComponentsBuilder.fromHttpUrl(uri.toString()).queryParam("limit", 100).queryParam("offset", 0)
				.queryParam("tenantId", tenantId);
	}
}
