package digit.academy.tutorial.web.controllers;

import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
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

	public ClerkApiController(AdvocateClerkService advocateClerkService, ResponseInfoFactory responseInfoFactory) {
		this.advocateClerkService = advocateClerkService;
		this.responseInfoFactory = responseInfoFactory;
	}

	/**
	 * Endpoint to handle the creation of advocate clerk registrations. Accepts a
	 * request containing clerk details and metadata.
	 *
	 * @param clerkRequest The request object containing clerk details and
	 *                     RequestInfo.
	 * @return ResponseEntity containing the AdvocateClerkResponse with status
	 *         ACCEPTED.
	 */
	@PostMapping(value = "/v1/_create")
	public ResponseEntity<AdvocateClerkResponse> clerkV1CreatePost(
			@Parameter(in = ParameterIn.DEFAULT, description = "Details for the user registration + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody AdvocateClerkRequest clerkRequest) {

		List<AdvocateClerk> clerks = advocateClerkService.registerAdvocateClerkRequest(clerkRequest);
		AdvocateClerkResponse response = getAdvocateClerkResponse(clerkRequest.getRequestInfo(), clerks);
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

	/**
	 * Endpoint to search for registered advocate clerks based on given criteria.
	 * Accepts a search request containing search parameters and metadata.
	 *
	 * @param searchRequest The request object containing search criteria and
	 *                      RequestInfo.
	 * @return ResponseEntity containing the AdvocateClerkResponse with status OK.
	 */
	@PostMapping(value = "/v1/_search")
	public ResponseEntity<AdvocateClerkResponse> clerkV1SearchPost(
			@Parameter(in = ParameterIn.DEFAULT, description = "Search criteria + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody AdvocateClerkSearchRequest searchRequest) {

		List<AdvocateClerk> clerks = advocateClerkService.searchAdvocateClerkRegistration(searchRequest);
		AdvocateClerkResponse response = getAdvocateClerkResponse(searchRequest.getRequestInfo(), clerks);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Endpoint to update the registration details of an advocate clerk. Accepts a
	 * request containing updated clerk details and metadata.
	 *
	 * @param clerkRequest The request object containing updated clerk details and
	 *                     RequestInfo.
	 * @return ResponseEntity containing the AdvocateClerkResponse with status
	 *         ACCEPTED.
	 */
	@PostMapping(value = "/v1/_update")
	public ResponseEntity<AdvocateClerkResponse> clerkV1UpdatePost(
			@Parameter(in = ParameterIn.DEFAULT, description = "Details of the registered advocate + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody AdvocateClerkRequest clerkRequest) {

		AdvocateClerk clerks = advocateClerkService.updateAdvocateClerkRegistration(clerkRequest);
		AdvocateClerkResponse response = getAdvocateClerkResponse(clerkRequest.getRequestInfo(),
				Collections.singletonList(clerks));
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

	/**
	 * Helper method to construct an AdvocateClerkResponse object. Combines
	 * ResponseInfo and a list of clerks into a single response.
	 *
	 * @param requestInfo Metadata related to the request.
	 * @param clerks      List of clerks to include in the response.
	 * @return AdvocateClerkResponse containing the clerks and response metadata.
	 */
	private AdvocateClerkResponse getAdvocateClerkResponse(RequestInfo requestInfo, List<AdvocateClerk> clerks) {
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
		return AdvocateClerkResponse.builder().clerks(clerks).responseInfo(responseInfo).build();
	}
}
