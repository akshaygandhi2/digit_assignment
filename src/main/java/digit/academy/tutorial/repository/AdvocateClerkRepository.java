package digit.academy.tutorial.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import digit.academy.tutorial.repository.querybuilder.AdvocateClerkQueryBuilder;
import digit.academy.tutorial.repository.rowmapper.AdvocateClerkRowMapper;
import digit.academy.tutorial.web.models.AdvocateClerk;
import digit.academy.tutorial.web.models.AdvocateClerkSearchCriteria;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class AdvocateClerkRepository {

	private final AdvocateClerkQueryBuilder queryBuilder;
	private final JdbcTemplate jdbcTemplate;
	private final AdvocateClerkRowMapper rowMapper;

	@Autowired
	public AdvocateClerkRepository(AdvocateClerkQueryBuilder queryBuilder, JdbcTemplate jdbcTemplate,
			AdvocateClerkRowMapper rowMapper) {
		this.queryBuilder = queryBuilder;
		this.jdbcTemplate = jdbcTemplate;
		this.rowMapper = rowMapper;
	}

	@SuppressWarnings("deprecation")
	public List<AdvocateClerk> getAdvocateClerkRegistrations(AdvocateClerkSearchCriteria searchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getAdvocateClerkRegistrationSearchQuery(searchCriteria, preparedStmtList);
		log.info("Final query: " + query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
	}
}
