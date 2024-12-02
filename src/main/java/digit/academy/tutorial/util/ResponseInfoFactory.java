package digit.academy.tutorial.util;

import static digit.academy.tutorial.config.ServiceConstants.FAILED;
import static digit.academy.tutorial.config.ServiceConstants.RES_MSG_ID;
import static digit.academy.tutorial.config.ServiceConstants.SUCCESSFUL;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

@Component
public class ResponseInfoFactory {

	public ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo, final Boolean success) {

		final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
		final String ver = requestInfo != null ? requestInfo.getVer() : "";
		Long ts = null;
		if (requestInfo != null)
			ts = requestInfo.getTs();
		final String resMsgId = RES_MSG_ID; // FIXME : Hard-coded
		final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
		final String responseStatus = Boolean.TRUE.equals(success) ? SUCCESSFUL : FAILED;

		return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).resMsgId(resMsgId).msgId(msgId).resMsgId(resMsgId)
				.status(responseStatus).build();
	}

}