package digit.academy.tutorial.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QueryBuilderUtil {
	
	private QueryBuilderUtil() {
	}

	/**
     * Builds the WHERE clause of a SQL query based on the provided conditions.
     * This method iterates through the conditionMap and adds conditions to the 
     * queryBuilder and preparedStmtList. It adds the "WHERE" keyword for the 
     * first condition and "AND" for subsequent conditions.
     *
     * @param queryBuilder the StringBuilder object used to build the query
     * @param preparedStmtList the list of prepared statement parameters
     * @param conditionMap the map containing condition keys and their corresponding values
     */
	public static void buildWhereClause(StringBuilder queryBuilder, List<Object> preparedStmtList,
			Map<String, Object> conditionMap) {

		for (Map.Entry<String, Object> entry : conditionMap.entrySet()) {
			if (!ObjectUtils.isEmpty(entry.getValue())) {
				if (CollectionUtils.isEmpty(preparedStmtList)) {
					queryBuilder.append(" WHERE ");
				} else {
					queryBuilder.append(" AND ");
				}
				queryBuilder.append(entry.getKey()).append(" = ? ");
				preparedStmtList.add(entry.getValue());
			}
		}
	}
}
