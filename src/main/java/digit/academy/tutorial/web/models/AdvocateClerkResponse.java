package digit.academy.tutorial.web.models;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AdvocateClerkResponse
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-20T15:58:36.708336466+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvocateClerkResponse {

	@JsonProperty("responseInfo")
	@Valid
	private ResponseInfo responseInfo = null;

	@JsonProperty("clerks")
	@Valid
	private List<AdvocateClerk> clerks = null;

	@JsonProperty("pagination")
	@Valid
	private Pagination pagination = null;

	public AdvocateClerkResponse addClerksItem(AdvocateClerk clerksItem) {
		if (this.clerks == null) {
			this.clerks = new ArrayList<>();
		}
		this.clerks.add(clerksItem);
		return this;
	}

}
