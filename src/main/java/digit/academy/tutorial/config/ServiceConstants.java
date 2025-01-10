package digit.academy.tutorial.config;

import org.springframework.stereotype.Component;

@Component
public class ServiceConstants {

	private ServiceConstants() {
	}

	public static final String EXTERNAL_SERVICE_EXCEPTION = "External Service threw an Exception: ";
	public static final String SEARCHER_SERVICE_EXCEPTION = "Exception while fetching from searcher: ";

	public static final String IDGEN_ERROR = "IDGEN ERROR";
	public static final String NO_IDS_FOUND_ERROR = "No ids returned from idgen Service";

	public static final String ERROR_WHILE_FETCHING_FROM_MDMS = "Exception occurred while fetching category lists from mdms: ";

	public static final String RES_MSG_ID = "uief87324";
	public static final String SUCCESSFUL = "successful";
	public static final String FAILED = "failed";

	public static final String URL = "url";
	public static final String URL_SHORTENING_ERROR_CODE = "URL_SHORTENING_ERROR";
	public static final String URL_SHORTENING_ERROR_MESSAGE = "Unable to shorten url: ";

	public static final String DOB_FORMAT_Y_M_D = "yyyy-MM-dd";
	public static final String DOB_FORMAT_D_M_Y = "dd/MM/yyyy";
	public static final String ILLEGAL_ARGUMENT_EXCEPTION_CODE = "IllegalArgumentException";
	public static final String OBJECTMAPPER_UNABLE_TO_CONVERT = "ObjectMapper not able to convertValue in userCall";
	public static final String DOB_FORMAT_D_M_Y_H_M_S = "dd-MM-yyyy HH:mm:ss";
	public static final String CREATED_DATE = "createdDate";
	public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	public static final String DOB = "dob";
	public static final String PWD_EXPIRY_DATE = "pwdExpiryDate";
	public static final String INVALID_DATE_FORMAT_CODE = "INVALID_DATE_FORMAT";
	public static final String INVALID_DATE_FORMAT_MESSAGE = "Failed to parse date format in user";
	public static final String CITIZEN_UPPER = "CITIZEN";
	public static final String CITIZEN_LOWER = "Citizen";
	public static final String USER = "user";

	public static final String PARSING_ERROR = "PARSING ERROR";
	public static final String FAILED_TO_PARSE_BUSINESS_SERVICE_SEARCH = "Failed to parse response of workflow business service search";
	public static final String BUSINESS_SERVICE_NOT_FOUND = "BUSINESSSERVICE_NOT_FOUND";
	public static final String THE_BUSINESS_SERVICE = "The businessService ";
	public static final String NOT_FOUND = " is not found";
	public static final String TENANTID = "?tenantId=";
	public static final String BUSINESS_IDS = "&businessIds=";
	public static final String BUSINESS_SERVICES = "&businessServices=";

	public static final String ADV_CREATE_EX = "ADV_CREATE_EX";
	public static final String ADV_CREATE_ERROR = "Error occurred while creating advocate: ";
	public static final String ADV_SEARCH_EX = "ADV_SEARCH_EX";
	public static final String ADV_SEARCH_ERROR = "Error occurred while searching advocate: ";
	public static final String ADV_UPDATE_EX = "ADV_UPDATE_EX";
	public static final String ADV_UPDATE_ERROR = "Error occurred while updating advocate: ";
	
	public static final String CLERK_CREATE_EX = "CLERK_CREATE_EX";
	public static final String CLERK_CREATE_ERROR = "Error occurred while creating clerk: ";
	public static final String CLERK_SEARCH_EX = "CLERK_SEARCH_EX";
	public static final String CLERK_SEARCH_ERROR = "Error occurred while searching clerk: ";
	public static final String CLERK_UPDATE_EX = "CLERK_UPDATE_EX";
	public static final String CLERK_UPDATE_ERROR = "Error occurred while updating clerk: ";

	public static final String REQUEST_INFO_NOT_VALID = "REQUEST_INFO_NOT_VALID";
	public static final String REQUEST_INFO_NOT_VALID_ERROR = "Request info or User infor are not valid";
	
	public static final String TENANT_ID_REQUIRED = "TENANT_ID_REQUIRED";
	public static final String TENANT_ID_REQUIRED_ERROR = "Tenant id is mandatory for registration";
	
	public static final String INDIVIDUAL_ID_REQUIRED = "INDIVIDUAL_ID_REQUIRED";
	public static final String INDIVIDUAL_ID_REQUIRED_ERROR = "Individual id is mandatory for registration";
	
	public static final String INDIVIDUAL_NOT_EXIST = "INDIVIDUAL_NOT_EXIST";
	public static final String INDIVIDUAL_NOT_EXIST_ERROR = "Individual does not exist";
	
	public static final String ADV_NOT_EXIST = "ADV_NOT_EXIST";
	public static final String ADV_NOT_EXIST_ERROR = "Advocate does not exist";
	
	public static final String CLERK_NOT_EXIST = "CLERK_NOT_EXIST";
	public static final String CLERK_NOT_EXIST_ERROR = "Advocate clerk does not exist";
}
