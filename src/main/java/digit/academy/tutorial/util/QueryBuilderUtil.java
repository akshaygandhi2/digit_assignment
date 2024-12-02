package digit.academy.tutorial.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QueryBuilderUtil {

	public void buildWhereClause(StringBuilder queryBuilder, List<Object> preparedStmtList,
			Map<String, Object> conditionMap) {

		boolean isFirst = true;
		for (Map.Entry<String, Object> entry : conditionMap.entrySet()) {
			if (!ObjectUtils.isEmpty(entry.getValue())) {
				if (isFirst) {
					queryBuilder.append(" WHERE ");
					isFirst = false;
				} else {
					queryBuilder.append(" AND ");
				}
				queryBuilder.append(entry.getKey()).append(" = ? ");
				preparedStmtList.add(entry.getValue());
			}
		}
	}
}
