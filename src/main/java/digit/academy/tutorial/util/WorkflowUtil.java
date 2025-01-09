package digit.academy.tutorial.util;

import static digit.academy.tutorial.config.ServiceConstants.BUSINESS_IDS;
import static digit.academy.tutorial.config.ServiceConstants.BUSINESS_SERVICE_NOT_FOUND;
import static digit.academy.tutorial.config.ServiceConstants.FAILED_TO_PARSE_BUSINESS_SERVICE_SEARCH;
import static digit.academy.tutorial.config.ServiceConstants.NOT_FOUND;
import static digit.academy.tutorial.config.ServiceConstants.PARSING_ERROR;
import static digit.academy.tutorial.config.ServiceConstants.TENANTID;
import static digit.academy.tutorial.config.ServiceConstants.THE_BUSINESS_SERVICE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.common.contract.models.RequestInfoWrapper;
import org.egov.common.contract.models.Workflow;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.contract.workflow.BusinessService;
import org.egov.common.contract.workflow.BusinessServiceResponse;
import org.egov.common.contract.workflow.ProcessInstance;
import org.egov.common.contract.workflow.ProcessInstanceRequest;
import org.egov.common.contract.workflow.ProcessInstanceResponse;
import org.egov.common.contract.workflow.State;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.repository.ServiceRequestRepository;
import digit.academy.tutorial.web.models.AdvocateClerkRequest;
import digit.academy.tutorial.web.models.AdvocateRequest;

@Service
public class WorkflowUtil {

	private final ServiceRequestRepository repository;
	private final ObjectMapper mapper;
	private final Configuration configs;

	public WorkflowUtil(ServiceRequestRepository repository, ObjectMapper mapper, Configuration configs) {
		this.repository = repository;
		this.mapper = mapper;
		this.configs = configs;
	}

	/**
	 * Updates the workflow status for the given advocate request.
	 * This method checks if the workflow feature is enabled in the configuration. If enabled,
	 * it iterates over the list of advocates in the provided request, updating the workflow status
	 * for each advocate.
	 *
	 * @param advocateRequest The request containing advocate details and metadata.
	 */
	public void updateAdvocateWorkflowStatus(AdvocateRequest advocateRequest) {
		if (Boolean.FALSE.equals(configs.getIsWorkflowEnabled())) {
			return;
		}
		advocateRequest.getAdvocates().forEach(advocate -> {
			updateWorkflowStatus(advocateRequest.getRequestInfo(), advocate.getTenantId(),
					advocate.getApplicationNumber(), "ADR", advocate.getWorkflow(), configs.getModuleName());
		});
	}

	/**
	 * Updates the workflow status for the given advocate clerk request.
	 * Similar to the updateAdvocateWorkflowStatus method, it checks if the workflow feature is enabled.
	 * If enabled, it updates the workflow status for each advocate clerk in the provided request.
	 *
	 * @param clerkRequest The request containing advocate clerk details and metadata.
	 */
	public void updateAdvocateClerkWorkflowStatus(AdvocateClerkRequest clerkRequest) {
		if (Boolean.FALSE.equals(configs.getIsWorkflowEnabled())) {
			return;
		}
		clerkRequest.getClerks().forEach(advocate -> {
			updateWorkflowStatus(clerkRequest.getRequestInfo(), advocate.getTenantId(), advocate.getApplicationNumber(),
					"ADCR", advocate.getWorkflow(), configs.getModuleName());
		});
	}

	/**
	 * Retrieves the workflow details for a list of business IDs (application numbers).
	 * This method checks if the workflow feature is enabled. If enabled, it fetches the process instances
	 * corresponding to the provided business IDs and returns the associated workflow details.
	 *
	 * @param requestInfo Request metadata containing details like user info.
	 * @param tenantId The tenant ID under which the workflows are being fetched.
	 * @param businessIds A list of business IDs (e.g., application numbers) for which workflows are to be retrieved.
	 * @return A map containing business IDs as keys and their respective workflow details as values.
	 */
	public Map<String, Workflow> getWorkflows(RequestInfo requestInfo, String tenantId, List<String> businessIds) {
		if (Boolean.FALSE.equals(configs.getIsWorkflowEnabled())) {
			return Collections.emptyMap();
		}
		List<ProcessInstance> processInstance = getProcessInstances(requestInfo, tenantId, businessIds);
		return getWorkflow(processInstance);
	}

	/**
	 * Searches the BussinessService corresponding to the businessServiceCode
	 * Returns applicable BussinessService for the given parameters
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param businessServiceCode
	 * @return
	 */
	public BusinessService getBusinessService(RequestInfo requestInfo, String tenantId, String businessServiceCode) {

		StringBuilder url = getSearchURLWithParams(tenantId, businessServiceCode);
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		Object result = repository.fetchResult(url, requestInfoWrapper);
		BusinessServiceResponse response = null;
		try {
			response = mapper.convertValue(result, BusinessServiceResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException(PARSING_ERROR, FAILED_TO_PARSE_BUSINESS_SERVICE_SEARCH);
		}

		if (CollectionUtils.isEmpty(response.getBusinessServices()))
			throw new CustomException(BUSINESS_SERVICE_NOT_FOUND,
					THE_BUSINESS_SERVICE + businessServiceCode + NOT_FOUND);

		return response.getBusinessServices().get(0);
	}

	/**
	 * Calls the workflow service with the given action and updates the status
	 * Returns the updated status of the application
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param businessId
	 * @param businessServiceCode
	 * @param workflow
	 * @param wfModuleName
	 * @return
	 */
	public String updateWorkflowStatus(RequestInfo requestInfo, String tenantId, String businessId,
			String businessServiceCode, Workflow workflow, String wfModuleName) {
		ProcessInstance processInstance = getProcessInstanceForWorkflow(requestInfo, tenantId, businessId,
				businessServiceCode, workflow, wfModuleName);
		ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(requestInfo,
				Collections.singletonList(processInstance));
		State state = callWorkFlow(workflowRequest);

		return state.getApplicationStatus();
	}

	/**
	 * Creates url for search based on given tenantId and businessIds
	 * 
	 * @param tenantId
	 * @param businessIds
	 * @return
	 */
	private StringBuilder getSearchURLWithParams(String tenantId, List<String> businessIds) {
		StringBuilder url = new StringBuilder(configs.getWfHost());
		url.append(configs.getWfProcessInstanceSearchPath());
		url.append(TENANTID);
		url.append(tenantId);
		url.append(BUSINESS_IDS);
		url.append(businessIds);
		return url;
	}

	/**
	 * Creates url for search based on given tenantId and businessServices
	 * 
	 * @param tenantId
	 * @param businessServices
	 * @return
	 */
	private StringBuilder getSearchURLWithParams(String tenantId, String businessServices) {
		StringBuilder url = new StringBuilder(configs.getWfHost());
		url.append(configs.getWfProcessInstanceSearchPath());
		url.append(TENANTID);
		url.append(tenantId);
		url.append(BUSINESS_IDS);
		url.append(businessServices);
		return url;
	}

	/**
	 * Enriches ProcessInstance Object for Workflow
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param businessId
	 * @param businessServiceCode
	 * @param workflow
	 * @param wfModuleName
	 * @return
	 */
	private ProcessInstance getProcessInstanceForWorkflow(RequestInfo requestInfo, String tenantId, String businessId,
			String businessServiceCode, Workflow workflow, String wfModuleName) {

		ProcessInstance processInstance = new ProcessInstance();
		processInstance.setBusinessId(businessId);
		processInstance.setAction(workflow.getAction());
		processInstance.setModuleName(wfModuleName);
		processInstance.setTenantId(tenantId);
		processInstance.setBusinessService(
				getBusinessService(requestInfo, tenantId, businessServiceCode).getBusinessService());
		processInstance.setComment(workflow.getComments());

		if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
			List<User> users = new ArrayList<>();

			workflow.getAssignes().forEach(uuid -> {
				User user = new User();
				user.setUuid(uuid);
				users.add(user);
			});

			processInstance.setAssignes(users);
		}

		return processInstance;
	}

	/**
	 * Gets the workflow corresponding to the processInstance
	 * 
	 * @param processInstances
	 * @return
	 */
	public Map<String, Workflow> getWorkflow(List<ProcessInstance> processInstances) {

		Map<String, Workflow> businessIdToWorkflow = new HashMap<>();

		processInstances.forEach(processInstance -> {
			List<String> userIds = null;

			if (!CollectionUtils.isEmpty(processInstance.getAssignes())) {
				userIds = processInstance.getAssignes().stream().map(User::getUuid).collect(Collectors.toList());
			}

			Workflow workflow = Workflow.builder().action(processInstance.getAction()).assignes(userIds)
					.comments(processInstance.getComment()).build();

			businessIdToWorkflow.put(processInstance.getBusinessId(), workflow);
		});

		return businessIdToWorkflow;
	}

	/**
	 * Searches the ProcessInstance corresponding to the businessServiceCode Returns
	 * applicable ProcessInstance for the given parameters
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param businessId
	 * @return
	 */
	public List<ProcessInstance> getProcessInstances(RequestInfo requestInfo, String tenantId,
			List<String> businessId) {

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

		StringBuilder url = getSearchURLWithParams(tenantId, businessId);

		Object res = repository.fetchResult(url, requestInfoWrapper);
		ProcessInstanceResponse response = null;

		try {
			response = mapper.convertValue(res, ProcessInstanceResponse.class);
		} catch (Exception e) {
			throw new CustomException(PARSING_ERROR, FAILED_TO_PARSE_BUSINESS_SERVICE_SEARCH);
		}

		if (response != null && !CollectionUtils.isEmpty(response.getProcessInstances())) {
			return response.getProcessInstances();
		}
		return Collections.emptyList();
	}

	/**
	 * Method to take the ProcessInstanceRequest as parameter and set resultant
	 * status
	 * 
	 * @param workflowReq
	 * @return
	 */
	private State callWorkFlow(ProcessInstanceRequest workflowReq) {
		ProcessInstanceResponse response = null;
		StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
		Object optional = repository.fetchResult(url, workflowReq);
		response = mapper.convertValue(optional, ProcessInstanceResponse.class);
		return response.getProcessInstances().get(0).getState();
	}
}