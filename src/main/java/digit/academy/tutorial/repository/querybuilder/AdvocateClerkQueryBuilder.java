package digit.academy.tutorial.repository.querybuilder;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import digit.academy.tutorial.util.QueryBuilderUtil;
import digit.academy.tutorial.web.models.AdvocateClerkSearchCriteria;

@Component
public class AdvocateClerkQueryBuilder {

	private static final String BASE_ADVOCATE_CLERK_QUERY = " SELECT ac.id as adv_clerk_id, ac.tenant_id as tenant_id, ac.application_number as application_number, ac.state_regn_number as state_regn_number, ac.individual_id as individual_id, ac.is_active as is_active, ac.additional_details as additional_details, ac.created_by as created_by, ac.last_modified_by as last_modified_by, ac.created_time as created_time, ac.last_modified_time as last_modified_time, ";
	private static final String DOCUMENT_SELECT_QUERY = " doc.id as doc_id, doc.document_type as document_type, doc.file_store as file_store, doc.document_uid as document_uid, doc.advocate_clerk_id as advocate_clerk_id, doc.additional_details as additional_details ";
	private static final String FROM_TABLES = " FROM eg_advocate_clerk_registration ac LEFT JOIN eg_adv_document doc ON ac.id = doc.advocate_clerk_id ";
	private static final String ORDERBY_CREATEDTIME = " ORDER BY ac.created_time DESC ";

	/**
     * This method constructs the SQL query to search for advocate clerk registrations
     * based on the provided search criteria and dynamically builds the WHERE clause
     * based on non-null search parameters.
     *
     * @param criteria         The search criteria containing various filters for the query.
     * @param preparedStmtList A list to hold the prepared statement values.
     * @return A string representing the constructed SQL query.
     */
	public String getAdvocateClerkRegistrationSearchQuery(AdvocateClerkSearchCriteria criteria,
			List<Object> preparedStmtList) {
		StringBuilder queryBuilder = new StringBuilder(BASE_ADVOCATE_CLERK_QUERY);
		queryBuilder.append(DOCUMENT_SELECT_QUERY);
		queryBuilder.append(FROM_TABLES);

		Map<String, Object> conditionMap = Map.of("ac.id", criteria.getId(), "ac.application_number",
				criteria.getApplicationNumber(), "ac.state_regn_number", criteria.getStateRegnNumber());

		QueryBuilderUtil.buildWhereClause(queryBuilder, preparedStmtList, conditionMap);
		queryBuilder.append(ORDERBY_CREATEDTIME);
		return queryBuilder.toString();
	}
}
