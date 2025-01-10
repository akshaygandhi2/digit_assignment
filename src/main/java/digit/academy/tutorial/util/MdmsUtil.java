package digit.academy.tutorial.util;

import static digit.academy.tutorial.config.ServiceConstants.ERROR_WHILE_FETCHING_FROM_MDMS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.academy.tutorial.config.Configuration;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Slf4j
@Component
public class MdmsUtil {

	private final RestTemplate restTemplate;
	private final ObjectMapper mapper;
	private final Configuration configs;

	public MdmsUtil(RestTemplate restTemplate, ObjectMapper mapper, Configuration configs) {
		this.restTemplate = restTemplate;
		this.mapper = mapper;
		this.configs = configs;
	}

	/**
	 * This method fetches MDMS data from the MDMS service based on provided criteria.
	 * It constructs the MDMS request and processes the response.
	 *
	 * @param requestInfo    The request information containing metadata.
	 * @param tenantId      The tenant ID for the request.
	 * @param moduleName    The module name for which data is to be fetched.
	 * @param masterNameList A list of master names for the query.
	 * @return Map<String, Map<String, JSONArray>> The MDMS response data organized by master names.
	 */
	public Map<String, Map<String, JSONArray>> fetchMdmsData(RequestInfo requestInfo, String tenantId,
			String moduleName, List<String> masterNameList) {

		StringBuilder uri = new StringBuilder();
		uri.append(configs.getMdmsHost()).append(configs.getMdmsEndPoint());
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequest(requestInfo, tenantId, moduleName, masterNameList);
		Object response = new HashMap<>();
		MdmsResponse mdmsResponse = new MdmsResponse();
		try {
			response = restTemplate.postForObject(uri.toString(), mdmsCriteriaReq, Map.class);
			mdmsResponse = mapper.convertValue(response, MdmsResponse.class);
		} catch (Exception e) {
			log.error(ERROR_WHILE_FETCHING_FROM_MDMS, e);
		}

		return mdmsResponse.getMdmsRes();
	}

	/**
	 * This method constructs the MDMS request object with the provided criteria.
	 * It prepares a list of master details, sets the module name, and organizes the MDMS request.
	 *
	 * @param requestInfo    The request information containing metadata.
	 * @param tenantId      The tenant ID for the request.
	 * @param moduleName    The module name for which data is to be fetched.
	 * @param masterNameList A list of master names for the query.
	 * @return MdmsCriteriaReq The constructed MDMS request object.
	 */
	private MdmsCriteriaReq getMdmsRequest(RequestInfo requestInfo, String tenantId, String moduleName,
			List<String> masterNameList) {

		List<MasterDetail> masterDetailList = new ArrayList<>();
		for (String masterName : masterNameList) {
			MasterDetail masterDetail = new MasterDetail();
			masterDetail.setName(masterName);
			masterDetailList.add(masterDetail);
		}

		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setMasterDetails(masterDetailList);
		moduleDetail.setModuleName(moduleName);
		List<ModuleDetail> moduleDetailList = new ArrayList<>();
		moduleDetailList.add(moduleDetail);

		MdmsCriteria mdmsCriteria = new MdmsCriteria();
		mdmsCriteria.setTenantId(tenantId.split("\\.")[0]);
		mdmsCriteria.setModuleDetails(moduleDetailList);

		MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
		mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
		mdmsCriteriaReq.setRequestInfo(requestInfo);

		return mdmsCriteriaReq;
	}
}