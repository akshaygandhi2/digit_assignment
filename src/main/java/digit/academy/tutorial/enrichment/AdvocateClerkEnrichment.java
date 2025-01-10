package digit.academy.tutorial.enrichment;

import java.util.List;

import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.models.Document;
import org.egov.common.utils.AuditDetailsEnrichmentUtil;
import org.egov.common.utils.UUIDEnrichmentUtil;
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

	public AdvocateClerkEnrichment(IdgenUtil idegenUtil, Configuration config) {
		this.idegenUtil = idegenUtil;
		this.config = config;
	}

	/**
     * Enriches the advocate clerk registration request by generating application numbers, 
     * enriching audit details, UUIDs, and setting default values for clerk data.
     * It also processes any associated documents.
     *
     * @param advocateClerkRequest the request containing advocate clerk details to be enriched
     */
	public void enrichAdvocateClerkRegistration(AdvocateClerkRequest advocateClerkRequest) {
		List<AdvocateClerk> clerks = advocateClerkRequest.getClerks();

		// Call idgen service to get the advocate application number
		List<String> advocateClerkIdList = idegenUtil.getIdList(advocateClerkRequest.getRequestInfo(),
				clerks.get(0).getTenantId(), config.getAdvClerkIdName(), config.getAdvClerkIdFormat(), clerks.size());

		for (int i = 0; i < clerks.size(); i++) {
			AdvocateClerk advocateClerk = clerks.get(i);
			// Enrich audit details
			AuditDetails auditDetails = AuditDetailsEnrichmentUtil.prepareAuditDetails(advocateClerk.getAuditDetails(),
					advocateClerkRequest.getRequestInfo(), Boolean.TRUE);
			advocateClerk.setAuditDetails(auditDetails);

			// Enrich UUID
			UUIDEnrichmentUtil.enrichRandomUuid(advocateClerk, "id");

			// Enrich application number from Idgen
			advocateClerk.setApplicationNumber(advocateClerkIdList.get(i));

			// Setting false unless the application is approved
			advocateClerk.setIsActive(false);

			List<Document> documents = advocateClerk.getDocuments();
			if (!CollectionUtils.isEmpty(documents)) {
				documents.forEach(document -> UUIDEnrichmentUtil.enrichRandomUuid(document, "id"));
			}
		}
	}

	/**
     * Enriches the advocate clerk registration update request by updating audit details, 
     * particularly lastModifiedTime and lastModifiedBy fields.
     *
     * @param clerkRequest the request containing advocate clerk details to be updated
     */
	public void enrichAdvocateClerkRegistrationUpdate(AdvocateClerkRequest clerkRequest) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		AdvocateClerk clerk = clerkRequest.getClerks().get(0);
		AuditDetails auditDetails = AuditDetailsEnrichmentUtil.prepareAuditDetails(clerk.getAuditDetails(),
				clerkRequest.getRequestInfo(), Boolean.FALSE);
		clerk.setAuditDetails(auditDetails);
	}
}
