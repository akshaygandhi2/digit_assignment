package digit.academy.tutorial.enrichment;

import java.util.List;

import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.models.Document;
import org.egov.common.utils.AuditDetailsEnrichmentUtil;
import org.egov.common.utils.UUIDEnrichmentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.util.IdgenUtil;
import digit.academy.tutorial.web.models.Advocate;
import digit.academy.tutorial.web.models.AdvocateRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AdvocateEnrichment {

	private final IdgenUtil idegenUtil;
	private final Configuration config;

	@Autowired
	public AdvocateEnrichment(IdgenUtil idegenUtil, Configuration config) {
		this.idegenUtil = idegenUtil;
		this.config = config;
	}

	public void enrichAdvocateRegistration(AdvocateRequest advocateRequest) {
		List<Advocate> advocates = advocateRequest.getAdvocates();

		// Call idgen service to get the advocate application number
		List<String> advocateIdList = idegenUtil.getIdList(advocateRequest.getRequestInfo(),
				advocates.get(0).getTenantId(), config.getAdvIdName(), config.getAdvIdFormat(), advocates.size());

		int index = 0;
		for (Advocate advocate : advocates) {
			// Enrich audit details
			AuditDetails auditDetails = AuditDetailsEnrichmentUtil.prepareAuditDetails(advocate.getAuditDetails(),
					advocateRequest.getRequestInfo(), Boolean.TRUE);
			advocate.setAuditDetails(auditDetails);

			// Enrich UUID
			UUIDEnrichmentUtil.enrichRandomUuid(advocate, "id");

			// Enrich application number from Idgen
			advocate.setApplicationNumber(advocateIdList.get(index++));

			// Setting false unless the application is approved
			advocate.setIsActive(false);

			List<Document> documents = advocate.getDocuments();
			if (!CollectionUtils.isEmpty(documents)) {
				documents.forEach(document -> {
					UUIDEnrichmentUtil.enrichRandomUuid(document, "id");
				});
			}
		}
	}

	public void enrichAdvocateRegistrationUpdate(AdvocateRequest advocateRequest) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		Advocate advocate = advocateRequest.getAdvocates().get(0);
		AuditDetails auditDetails = AuditDetailsEnrichmentUtil.prepareAuditDetails(advocate.getAuditDetails(),
				advocateRequest.getRequestInfo(), Boolean.FALSE);
		advocate.setAuditDetails(auditDetails);
	}
}
