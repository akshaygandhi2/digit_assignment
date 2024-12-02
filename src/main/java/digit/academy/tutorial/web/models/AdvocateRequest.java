package digit.academy.tutorial.web.models;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AdvocateRequest
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-11-20T15:58:36.708336466+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvocateRequest {

	@JsonProperty("requestInfo")
	@Valid
	private RequestInfo requestInfo = null;

	@JsonProperty("advocates")
	@Valid
	private List<Advocate> advocates = null;

	public AdvocateRequest addAdvocatesItem(Advocate advocatesItem) {
		if (this.advocates == null) {
			this.advocates = new ArrayList<>();
		}
		this.advocates.add(advocatesItem);
		return this;
	}
}
