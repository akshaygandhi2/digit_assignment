package digit.academy.tutorial.util;

import static digit.academy.tutorial.config.ServiceConstants.BUSINESS_SERVICES;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.repository.ServiceRequestRepository;

@Service
public class WorkflowUtil {

	private final ServiceRequestRepository repository;
	private final ObjectMapper mapper;
	private final Configuration configs;

	@Autowired
	public WorkflowUtil(ServiceRequestRepository repository, ObjectMapper mapper, Configuration configs) {
		this.repository = repository;
		this.mapper = mapper;
		this.configs = configs;
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
	 * Creates url for search based on given tenantId and businessServices
	 * 
	 * @param tenantId
	 * @param businessService
	 * @return
	 */
	private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {
		StringBuilder url = new StringBuilder(configs.getWfHost());
		url.append(configs.getWfProcessInstanceSearchPath());
		url.append(TENANTID);
		url.append(tenantId);
		url.append(BUSINESS_SERVICES);
		url.append(businessService);
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
	public ProcessInstance getCurrentProcessInstance(RequestInfo requestInfo, String tenantId, String businessId) {

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

		StringBuilder url = getSearchURLWithParams(tenantId, businessId);

		Object res = repository.fetchResult(url, requestInfoWrapper);
		ProcessInstanceResponse response = null;

		try {
			response = mapper.convertValue(res, ProcessInstanceResponse.class);
		} catch (Exception e) {
			throw new CustomException(PARSING_ERROR, FAILED_TO_PARSE_BUSINESS_SERVICE_SEARCH);
		}

		if (response != null && !CollectionUtils.isEmpty(response.getProcessInstances())
				&& response.getProcessInstances().get(0) != null)
			return response.getProcessInstances().get(0);

		return null;
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