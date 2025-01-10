package digit.academy.tutorial.repository;

import static digit.academy.tutorial.config.ServiceConstants.EXTERNAL_SERVICE_EXCEPTION;
import static digit.academy.tutorial.config.ServiceConstants.SEARCHER_SERVICE_EXCEPTION;

import java.util.Map;

import org.egov.tracer.model.ServiceCallException;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ServiceRequestRepository {

	private final ObjectMapper mapper;
	private final RestTemplate restTemplate;

	public ServiceRequestRepository(ObjectMapper mapper, RestTemplate restTemplate) {
		this.mapper = mapper;
		this.restTemplate = restTemplate;
	}

	/**
     * Sends a POST request to an external service and fetches the result.
     * This method configures the ObjectMapper to avoid failing on empty beans, 
     * then sends the request to the specified URI and maps the response to a Map.
     * In case of errors, it logs the exception and throws a custom exception 
     * for client-side or service-related errors.
     *
     * @param uri the URI to which the request is to be sent
     * @param request the request object to be sent in the POST request
     * @return the response object mapped to a Map, or null in case of failure
     */
	public Object fetchResult(StringBuilder uri, Object request) {
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Object response = null;
		try {
			response = restTemplate.postForObject(uri.toString(), request, Map.class);
		} catch (HttpClientErrorException e) {
			log.error(EXTERNAL_SERVICE_EXCEPTION, e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error(SEARCHER_SERVICE_EXCEPTION, e);
		}

		return response;
	}
}