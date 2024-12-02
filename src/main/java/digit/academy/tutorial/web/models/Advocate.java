package digit.academy.tutorial.web.models;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.models.Document;
import org.egov.common.contract.models.Workflow;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Advocate
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-20T15:58:36.708336466+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Advocate {

	@JsonProperty("id")
	@Valid
	private String id = null;

	@JsonProperty("tenantId")
	@NotNull
	@Size(min = 2, max = 128)
	private String tenantId = null;

	@JsonProperty("applicationNumber")
	@Size(min = 2, max = 64)
	private String applicationNumber = null;

	@JsonProperty("barRegistrationNumber")
	@Pattern(regexp = "^[A-Z]{2,3}\\d+\\d{4}$", message = "Invalid BAR Council number. Your BAR Council number is of the following format: {STATE_CODE}{ROLL_NUMBER}{YEAR}")
	@Size(min = 2, max = 64)
	private String barRegistrationNumber = null;

	@JsonProperty("advocateType")
	@Size(min = 2, max = 64)
	private String advocateType = null;

	@JsonProperty("organisationID")
	@Valid
	private String organisationID = null;

	@JsonProperty("individualId")
	private String individualId = null;

	@JsonProperty("isActive")
	private Boolean isActive = true;

	@JsonProperty("workflow")
	@Valid
	private Workflow workflow = null;

	@JsonProperty("documents")
	@Valid
	private List<Document> documents = null;

	@JsonProperty("auditDetails")
	@Valid
	private AuditDetails auditDetails = null;

	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;

	public Advocate addDocumentsItem(Document documentsItem) {
		if (this.documents == null) {
			this.documents = new ArrayList<>();
		}
		this.documents.add(documentsItem);
		return this;
	}
}
