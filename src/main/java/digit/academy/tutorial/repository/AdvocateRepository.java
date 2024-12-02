package digit.academy.tutorial.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import digit.academy.tutorial.repository.querybuilder.AdvocateQueryBuilder;
import digit.academy.tutorial.repository.rowmapper.AdvocateRowMapper;
import digit.academy.tutorial.web.models.Advocate;
import digit.academy.tutorial.web.models.AdvocateSearchCriteria;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class AdvocateRepository {

	private final AdvocateQueryBuilder queryBuilder;
	private final JdbcTemplate jdbcTemplate;
	private final AdvocateRowMapper rowMapper;

	@Autowired
	public AdvocateRepository(AdvocateQueryBuilder queryBuilder, JdbcTemplate jdbcTemplate,
			AdvocateRowMapper rowMapper) {
		this.queryBuilder = queryBuilder;
		this.jdbcTemplate = jdbcTemplate;
		this.rowMapper = rowMapper;
	}

	@SuppressWarnings("deprecation")
	public List<Advocate> getAdvocates(AdvocateSearchCriteria searchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getAdvocateRegistrationSearchQuery(searchCriteria, preparedStmtList);
		log.info("Final query: " + query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
	}
}
