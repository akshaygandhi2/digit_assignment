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

import digit.academy.tutorial.service.AdvocateService;
import digit.academy.tutorial.util.ResponseInfoFactory;
import digit.academy.tutorial.web.models.Advocate;
import digit.academy.tutorial.web.models.AdvocateRequest;
import digit.academy.tutorial.web.models.AdvocateResponse;
import digit.academy.tutorial.web.models.AdvocateSearchRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-20T15:58:36.708336466+05:30[Asia/Kolkata]")
@Controller
@RequestMapping("/advocate")
@Slf4j
public class AdvocateApiController {

	private final AdvocateService advocateService;
	private final ResponseInfoFactory responseInfoFactory;

	public AdvocateApiController(AdvocateService advocateService, ResponseInfoFactory responseInfoFactory) {
		this.advocateService = advocateService;
		this.responseInfoFactory = responseInfoFactory;
	}

	/**
	 * Endpoint to handle the creation of advocate registrations. Accepts a request
	 * containing advocate details and metadata.
	 * 
	 * @param advocateRequest The request object containing advocate details and
	 *                        RequestInfo.
	 * @return ResponseEntity containing the AdvocateResponse with status ACCEPTED.
	 */
	@PostMapping(value = "/v1/_create")
	public ResponseEntity<AdvocateResponse> advocateV1CreatePost(
			@Parameter(in = ParameterIn.DEFAULT, description = "Details for the advocate registration + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody AdvocateRequest advocateRequest) {

		List<Advocate> advocates = advocateService.registerAdvocateRequest(advocateRequest);
		AdvocateResponse response = getAdvocateResponse(advocateRequest.getRequestInfo(), advocates);
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

	/**
	 * Endpoint to search for registered advocates based on given criteria. Accepts
	 * a search request containing search parameters and metadata.
	 *
	 * @param searchRequest The request object containing search criteria and
	 *                      RequestInfo.
	 * @return ResponseEntity containing the AdvocateResponse with status OK.
	 */
	@PostMapping(value = "/v1/_search")
	public ResponseEntity<AdvocateResponse> advocateV1SearchPost(
			@Parameter(in = ParameterIn.DEFAULT, description = "Search criteria + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody AdvocateSearchRequest searchRequest) {

		List<Advocate> advocates = advocateService.searchAdvocateRegistration(searchRequest);
		AdvocateResponse response = getAdvocateResponse(searchRequest.getRequestInfo(), advocates);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Endpoint to update the registration details of an advocate. Accepts a request
	 * containing updated advocate details and metadata.
	 *
	 * @param advocateRequest The request object containing updated advocate details
	 *                        and RequestInfo.
	 * @return ResponseEntity containing the AdvocateResponse with status ACCEPTED.
	 */
	@PostMapping(value = "/v1/_update")
	public ResponseEntity<AdvocateResponse> advocateV1UpdatePost(
			@Parameter(in = ParameterIn.DEFAULT, description = "Details of the registered advocate + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody AdvocateRequest advocateRequest) {

		Advocate advocate = advocateService.updateAdvocateRegistration(advocateRequest);
		AdvocateResponse response = getAdvocateResponse(advocateRequest.getRequestInfo(),
				Collections.singletonList(advocate));
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

	/**
	 * Helper method to construct an AdvocateResponse object. Combines ResponseInfo
	 * and a list of advocates into a single response.
	 *
	 * @param requestInfo Metadata related to the request.
	 * @param advocates   List of advocates to include in the response.
	 * @return AdvocateResponse containing the advocates and response metadata.
	 */
	private AdvocateResponse getAdvocateResponse(RequestInfo requestInfo, List<Advocate> advocates) {
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
		return AdvocateResponse.builder().advocates(advocates).responseInfo(responseInfo).build();
	}
}
