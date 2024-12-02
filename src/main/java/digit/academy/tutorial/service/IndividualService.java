package digit.academy.tutorial.service;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	public IndividualService(IndividualUtil individualUtil, ObjectMapper mapper) {
		this.individualUtil = individualUtil;
		this.mapper = mapper;
	}

	public boolean isIndividualExist(List<String> ids, RequestInfo requestInfo, String tenantId) {
		List<Individual> individuals = searchIndividuals(ids, requestInfo, tenantId);
		return !CollectionUtils.isEmpty(individuals) && individuals.size() > 0;
	}

	public List<Individual> searchIndividuals(List<String> ids, RequestInfo requestInfo, String tenantId) {
		Object response = individualUtil.fetchIndividualDetails(ids, requestInfo, tenantId);
		if (ObjectUtils.isEmpty(response)) {
			return Collections.emptyList();
		}
		IndividualBulkResponse individualResponse = mapper.convertValue(response, IndividualBulkResponse.class);
		return individualResponse.getIndividual();
	}
}
