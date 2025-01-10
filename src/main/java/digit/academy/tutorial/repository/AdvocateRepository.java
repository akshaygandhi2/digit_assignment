package digit.academy.tutorial.repository;

import java.util.List;

import digit.academy.tutorial.web.models.Advocate;
import digit.academy.tutorial.web.models.AdvocateSearchCriteria;

public interface AdvocateRepository {

	List<Advocate> getAdvocates(AdvocateSearchCriteria searchCriteria);
}
