package digit.academy.tutorial.util;

import org.egov.common.contract.models.AuditDetails;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuditDetailsUtil {

	public AuditDetails getAuditDetails(String by, Boolean isCreate) {
		Long currentTime = System.currentTimeMillis();

		if (Boolean.TRUE.equals(isCreate)) {
			return AuditDetails.builder().createdBy(by).createdTime(currentTime).lastModifiedBy(by)
					.lastModifiedTime(currentTime).build();
		}
		return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(currentTime).build();
	}
}
