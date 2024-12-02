package digit.academy.tutorial.service;

import java.util.Collections;
import java.util.Map;

import org.egov.common.contract.models.Workflow;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.workflow.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.util.WorkflowUtil;
import digit.academy.tutorial.web.models.AdvocateClerkRequest;
import digit.academy.tutorial.web.models.AdvocateRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorkflowService {

	private final Configuration config;
	private final WorkflowUtil workflowUtil;

	@Autowired
	public WorkflowService(Configuration config, WorkflowUtil workflowUtil) {
		this.config = config;
		this.workflowUtil = workflowUtil;
	}

	public void updateAdvocateWorkflowStatus(AdvocateRequest advocateRequest) {
		if (Boolean.FALSE.equals(config.getIsWorkflowEnabled())) {
			return;
		}
		advocateRequest.getAdvocates().forEach(advocate -> {
			workflowUtil.updateWorkflowStatus(advocateRequest.getRequestInfo(), advocate.getTenantId(),
					advocate.getApplicationNumber(), "ADR", advocate.getWorkflow(), config.getModuleName());
		});
	}

	public void updateAdvocateClerkWorkflowStatus(AdvocateClerkRequest clerkRequest) {
		if (Boolean.FALSE.equals(config.getIsWorkflowEnabled())) {
			return;
		}
		clerkRequest.getClerks().forEach(advocate -> {
			workflowUtil.updateWorkflowStatus(clerkRequest.getRequestInfo(), advocate.getTenantId(),
					advocate.getApplicationNumber(), "ADCR", advocate.getWorkflow(), config.getModuleName());
		});
	}

	public Workflow getCurrentWorkflow(RequestInfo requestInfo, String tenantId, String businessId) {
		if (Boolean.FALSE.equals(config.getIsWorkflowEnabled())) {
			return null;
		}
		ProcessInstance processInstance = workflowUtil.getCurrentProcessInstance(requestInfo, tenantId, businessId);
		Map<String, Workflow> workflowMap = workflowUtil.getWorkflow(Collections.singletonList(processInstance));
		return workflowMap.get(businessId);
	}
}
