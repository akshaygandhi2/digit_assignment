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
import digit.academy.tutorial.web.models.Advocate;
import digit.academy.tutorial.web.models.AdvocateRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AdvocateEnrichment {

	private final IdgenUtil idegenUtil;
	private final Configuration config;

	public AdvocateEnrichment(IdgenUtil idegenUtil, Configuration config) {
		this.idegenUtil = idegenUtil;
		this.config = config;
	}

	/**
	 * Enriches the advocate registration request by generating application numbers,
	 * enriching audit details, UUIDs, and setting default values for advocate data.
	 * It also processes any associated documents.
	 *
	 * @param advocateRequest the request containing advocate details to be enriched
	 */
	public void enrichAdvocateRegistration(AdvocateRequest advocateRequest) {
		List<Advocate> advocates = advocateRequest.getAdvocates();

		// Call idgen service to get the advocate application number
		List<String> advocateIdList = idegenUtil.getIdList(advocateRequest.getRequestInfo(),
				advocates.get(0).getTenantId(), config.getAdvIdName(), config.getAdvIdFormat(), advocates.size());

		for (int i = 0; i < advocates.size(); i++) {
			Advocate advocate = advocates.get(i);
			// Enrich audit details
			AuditDetails auditDetails = AuditDetailsEnrichmentUtil.prepareAuditDetails(advocate.getAuditDetails(),
					advocateRequest.getRequestInfo(), Boolean.TRUE);
			advocate.setAuditDetails(auditDetails);

			// Enrich UUID
			UUIDEnrichmentUtil.enrichRandomUuid(advocate, "id");

			// Enrich application number from Idgen
			advocate.setApplicationNumber(advocateIdList.get(i));

			// Setting false unless the application is approved
			advocate.setIsActive(false);

			List<Document> documents = advocate.getDocuments();
			if (!CollectionUtils.isEmpty(documents)) {
				documents.forEach(document -> UUIDEnrichmentUtil.enrichRandomUuid(document, "id"));
			}
		}
	}

	/**
	 * Enriches the advocate registration update request by updating audit details,
	 * particularly lastModifiedTime and lastModifiedBy fields.
	 *
	 * @param advocateRequest the request containing advocate details to be updated
	 */
	public void enrichAdvocateRegistrationUpdate(AdvocateRequest advocateRequest) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		Advocate advocate = advocateRequest.getAdvocates().get(0);
		AuditDetails auditDetails = AuditDetailsEnrichmentUtil.prepareAuditDetails(advocate.getAuditDetails(),
				advocateRequest.getRequestInfo(), Boolean.FALSE);
		advocate.setAuditDetails(auditDetails);
	}
}
