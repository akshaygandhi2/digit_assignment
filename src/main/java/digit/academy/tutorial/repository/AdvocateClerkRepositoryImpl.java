package digit.academy.tutorial.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import digit.academy.tutorial.repository.querybuilder.AdvocateClerkQueryBuilder;
import digit.academy.tutorial.repository.rowmapper.AdvocateClerkRowMapper;
import digit.academy.tutorial.web.models.AdvocateClerk;
import digit.academy.tutorial.web.models.AdvocateClerkSearchCriteria;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class AdvocateClerkRepositoryImpl implements AdvocateClerkRepository {

	private final AdvocateClerkQueryBuilder queryBuilder;
	private final JdbcTemplate jdbcTemplate;
	private final AdvocateClerkRowMapper rowMapper;

	public AdvocateClerkRepositoryImpl(AdvocateClerkQueryBuilder queryBuilder, JdbcTemplate jdbcTemplate,
			AdvocateClerkRowMapper rowMapper) {
		this.queryBuilder = queryBuilder;
		this.jdbcTemplate = jdbcTemplate;
		this.rowMapper = rowMapper;
	}

	/**
	 * Retrieves a list of advocate clerk registrations based on the provided search
	 * criteria. This method builds the query using the search criteria and executes
	 * it using the JdbcTemplate to fetch the results, which are then mapped to
	 * AdvocateClerk objects using the AdvocateClerkRowMapper.
	 *
	 * @param searchCriteria the criteria to filter and search for advocate clerk
	 *                       registrations
	 * @return a list of advocate clerks that match the search criteria
	 */
	@SuppressWarnings("deprecation")
	@Override
	public List<AdvocateClerk> getAdvocateClerkRegistrations(AdvocateClerkSearchCriteria searchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getAdvocateClerkRegistrationSearchQuery(searchCriteria, preparedStmtList);
		log.info("Final query: " + query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
	}
}
