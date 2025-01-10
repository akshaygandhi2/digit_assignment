package digit.academy.tutorial.repository;

import java.util.List;

import digit.academy.tutorial.web.models.AdvocateClerk;
import digit.academy.tutorial.web.models.AdvocateClerkSearchCriteria;

public interface AdvocateClerkRepository {

	List<AdvocateClerk> getAdvocateClerkRegistrations(AdvocateClerkSearchCriteria searchCriteria);
}
