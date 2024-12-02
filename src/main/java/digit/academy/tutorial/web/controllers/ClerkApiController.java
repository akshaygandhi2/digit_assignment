package digit.academy.tutorial.web.controllers;

import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import digit.academy.tutorial.service.AdvocateClerkService;
import digit.academy.tutorial.util.ResponseInfoFactory;
import digit.academy.tutorial.web.models.AdvocateClerk;
import digit.academy.tutorial.web.models.AdvocateClerkRequest;
import digit.academy.tutorial.web.models.AdvocateClerkResponse;
import digit.academy.tutorial.web.models.AdvocateClerkSearchRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-20T15:58:36.708336466+05:30[Asia/Kolkata]")
@Controller
@RequestMapping("/advocate/clerk")
@Slf4j
public class ClerkApiController {

	private final AdvocateClerkService advocateClerkService;
	private final ResponseInfoFactory responseInfoFactory;

	@Autowired
	public ClerkApiController(AdvocateClerkService advocateClerkService, ResponseInfoFactory responseInfoFactory) {
		this.advocateClerkService = advocateClerkService;
		this.responseInfoFactory = responseInfoFactory;
	}

	@PostMapping(value = "/v1/_create")
	public ResponseEntity<AdvocateClerkResponse> clerkV1CreatePost(
			@Parameter(in = ParameterIn.DEFAULT, description = "Details for the user registration + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody AdvocateClerkRequest clerkRequest) {

		List<AdvocateClerk> clerks = advocateClerkService.registerAdvocateClerkRequest(clerkRequest);
		return getAdvocateClerkResponse(clerkRequest.getRequestInfo(), clerks);
	}

	@PostMapping(value = "/v1/_search")
	public ResponseEntity<AdvocateClerkResponse> clerkV1SearchPost(
			@Parameter(in = ParameterIn.DEFAULT, description = "Search criteria + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody AdvocateClerkSearchRequest searchRequest) {

		List<AdvocateClerk> clerks = advocateClerkService.searchAdvocateClerkRegistration(searchRequest);
		return getAdvocateClerkResponse(searchRequest.getRequestInfo(), clerks);
	}

	@PostMapping(value = "/v1/_update")
	public ResponseEntity<AdvocateClerkResponse> clerkV1UpdatePost(
			@Parameter(in = ParameterIn.DEFAULT, description = "Details of the registered advocate + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody AdvocateClerkRequest clerkRequest) {

		AdvocateClerk clerks = advocateClerkService.updateAdvocateClerkRegistration(clerkRequest);
		return getAdvocateClerkResponse(clerkRequest.getRequestInfo(), Collections.singletonList(clerks));
	}

	private ResponseEntity<AdvocateClerkResponse> getAdvocateClerkResponse(RequestInfo requestInfo,
			List<AdvocateClerk> clerks) {
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
		AdvocateClerkResponse response = AdvocateClerkResponse.builder().clerks(clerks).responseInfo(responseInfo)
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
