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
import digit.academy.tutorial.web.models.AdvocateClerk;
import digit.academy.tutorial.web.models.AdvocateClerkRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AdvocateClerkEnrichment {

	private final IdgenUtil idegenUtil;
	private final Configuration config;

	@Autowired
	public AdvocateClerkEnrichment(IdgenUtil idegenUtil, Configuration config) {
		this.idegenUtil = idegenUtil;
		this.config = config;
	}

	public void enrichAdvocateClerkRegistration(AdvocateClerkRequest advocateClerkRequest) {
		List<AdvocateClerk> clerks = advocateClerkRequest.getClerks();

		// Call idgen service to get the advocate application number
		List<String> advocateClerkIdList = idegenUtil.getIdList(advocateClerkRequest.getRequestInfo(),
				clerks.get(0).getTenantId(), config.getAdvClerkIdName(), config.getAdvClerkIdFormat(), clerks.size());

		int index = 0;
		for (AdvocateClerk advocateClerk : clerks) {
			// Enrich audit details
			AuditDetails auditDetails = AuditDetailsEnrichmentUtil.prepareAuditDetails(advocateClerk.getAuditDetails(),
					advocateClerkRequest.getRequestInfo(), Boolean.TRUE);
			advocateClerk.setAuditDetails(auditDetails);

			// Enrich UUID
			UUIDEnrichmentUtil.enrichRandomUuid(advocateClerk, "id");

			// Enrich application number from Idgen
			advocateClerk.setApplicationNumber(advocateClerkIdList.get(index++));

			// Setting false unless the application is approved
			advocateClerk.setIsActive(false);

			List<Document> documents = advocateClerk.getDocuments();
			if (!CollectionUtils.isEmpty(documents)) {
				documents.forEach(document -> {
					UUIDEnrichmentUtil.enrichRandomUuid(document, "id");
				});
			}
		}
	}

	public void enrichAdvocateClerkRegistrationUpdate(AdvocateClerkRequest clerkRequest) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		AdvocateClerk clerk = clerkRequest.getClerks().get(0);
		AuditDetails auditDetails = AuditDetailsEnrichmentUtil.prepareAuditDetails(clerk.getAuditDetails(),
				clerkRequest.getRequestInfo(), Boolean.FALSE);
		clerk.setAuditDetails(auditDetails);
	}
}
