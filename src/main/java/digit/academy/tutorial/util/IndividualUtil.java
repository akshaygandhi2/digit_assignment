package digit.academy.tutorial.util;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.models.individual.IndividualSearch;
import org.egov.common.models.individual.IndividualSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	public IndividualUtil(Configuration config, ServiceRequestRepository requestRepo) {
		this.config = config;
		this.requestRepo = requestRepo;
	}

	/**
	 * fetch the individual details from individual service
	 *
	 * @param ids
	 * @param requestInfo
	 * @param tenantId
	 */
	public Object fetchIndividualDetails(List<String> ids, RequestInfo requestInfo, String tenantId) {
		String uri = getSearchURLWithParams(tenantId).toUriString();

		IndividualSearch individualSearch = IndividualSearch.builder().id(ids).build();
		IndividualSearchRequest individualSearchRequest = IndividualSearchRequest.builder().requestInfo(requestInfo)
				.individual(individualSearch).build();

		log.info("IndividualUtil::fetchIndividualDetails::call individual search with tenantId::" + tenantId
				+ "::individual ids::" + ids);

		Object response = requestRepo.fetchResult(new StringBuilder(uri), individualSearchRequest);
		return response;
	}

	private UriComponentsBuilder getSearchURLWithParams(String tenantId) {
		StringBuilder uri = new StringBuilder();
		uri.append(config.getIndividualHost()).append(config.getIndividualSearchEndpoint());
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(uri.toString()).queryParam("limit", 100)
				.queryParam("offset", 0).queryParam("tenantId", tenantId);

		return uriBuilder;
	}
}
