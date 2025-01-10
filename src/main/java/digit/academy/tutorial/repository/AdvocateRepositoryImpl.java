package digit.academy.tutorial.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import digit.academy.tutorial.repository.querybuilder.AdvocateQueryBuilder;
import digit.academy.tutorial.repository.rowmapper.AdvocateRowMapper;
import digit.academy.tutorial.web.models.Advocate;
import digit.academy.tutorial.web.models.AdvocateSearchCriteria;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class AdvocateRepositoryImpl implements AdvocateRepository {

	private final AdvocateQueryBuilder queryBuilder;
	private final JdbcTemplate jdbcTemplate;
	private final AdvocateRowMapper rowMapper;

	public AdvocateRepositoryImpl(AdvocateQueryBuilder queryBuilder, JdbcTemplate jdbcTemplate,
			AdvocateRowMapper rowMapper) {
		this.queryBuilder = queryBuilder;
		this.jdbcTemplate = jdbcTemplate;
		this.rowMapper = rowMapper;
	}

	/**
     * Retrieves a list of advocates based on the provided search criteria.
     * This method builds the query using the search criteria and executes it 
     * using the JdbcTemplate to fetch the results, which are then mapped 
     * to Advocate objects using the AdvocateRowMapper.
     *
     * @param searchCriteria the criteria to filter and search for advocates
     * @return a list of advocates that match the search criteria
     */
	@SuppressWarnings("deprecation")
	@Override
	public List<Advocate> getAdvocates(AdvocateSearchCriteria searchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getAdvocateRegistrationSearchQuery(searchCriteria, preparedStmtList);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
	}
}
