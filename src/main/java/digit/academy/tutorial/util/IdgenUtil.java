package digit.academy.tutorial.util;

import static digit.academy.tutorial.config.ServiceConstants.IDGEN_ERROR;
import static digit.academy.tutorial.config.ServiceConstants.NO_IDS_FOUND_ERROR;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.idgen.IdGenerationRequest;
import org.egov.common.contract.idgen.IdGenerationResponse;
import org.egov.common.contract.idgen.IdRequest;
import org.egov.common.contract.idgen.IdResponse;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.repository.ServiceRequestRepository;

@Component
public class IdgenUtil {

	private final ObjectMapper mapper;
	private final ServiceRequestRepository restRepo;
	private final Configuration configs;

	public IdgenUtil(ObjectMapper mapper, ServiceRequestRepository restRepo, Configuration configs) {
		this.mapper = mapper;
		this.restRepo = restRepo;
		this.configs = configs;
	}

	/**
     * This method fetches a list of unique IDs from the Idgen service.
     * It prepares a list of ID requests and invokes the external ID generation service.
     *
     * @param requestInfo  The request information containing metadata.
     * @param tenantId    The tenant ID for the request.
     * @param idName      The ID name to request from the ID generation service.
     * @param idformat    The format of the ID to be generated.
     * @param count       The number of IDs to generate.
     * @return List<String> List of generated IDs.
     * @throws CustomException If no IDs are returned by the IDgen service.
     */
	public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat,
			Integer count) {
		List<IdRequest> reqList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			reqList.add(IdRequest.builder().idName(idName).format(idformat).tenantId(tenantId).build());
		}

		IdGenerationRequest request = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo)
				.build();
		StringBuilder uri = new StringBuilder(configs.getIdGenHost()).append(configs.getIdGenPath());
		IdGenerationResponse response = mapper.convertValue(restRepo.fetchResult(uri, request),
				IdGenerationResponse.class);

		List<IdResponse> idResponses = response.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException(IDGEN_ERROR, NO_IDS_FOUND_ERROR);

		return idResponses.stream().map(IdResponse::getId).toList();
	}
}