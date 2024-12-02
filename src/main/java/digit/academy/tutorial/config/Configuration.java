package digit.academy.tutorial.config;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Data
@Import({ TracerConfiguration.class })
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Configuration {

	// User Config
	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.context.path}")
	private String userContextPath;

	@Value("${egov.user.create.path}")
	private String userCreateEndpoint;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	@Value("${egov.user.update.path}")
	private String userUpdateEndpoint;

	// Individual Config
	@Value("${egov.individual.host}")
	private String individualHost;

	@Value("${egov.individual.context.path}")
	private String individualContextPath;

	@Value("${egov.individual.create.path}")
	private String individualCreateEndpoint;

	@Value("${egov.individual.search.path}")
	private String individualSearchEndpoint;

	@Value("${egov.individual.update.path}")
	private String individualUpdateEndpoint;

	// Idgen Config
	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	// Workflow Config
	@Value("${is.workflow.enabled}")
	private Boolean isWorkflowEnabled;

	@Value("${egov.workflow.host}")
	private String wfHost;

	@Value("${egov.workflow.transition.path}")
	private String wfTransitionPath;

	@Value("${egov.workflow.businessservice.search.path}")
	private String wfBusinessServiceSearchPath;

	@Value("${egov.workflow.processinstance.search.path}")
	private String wfProcessInstanceSearchPath;

	// ADV Variables
	@Value("${adv.kafka.create.topic}")
	private String advCreateTopic;

	@Value("${adv.kafka.update.topic}")
	private String advUpdateTopic;

	@Value("${adv.idgen.id.name}")
	private String advIdName;

	@Value("${adv.idgen.id.format}")
	private String advIdFormat;

	// ADV Clerk Variables
	@Value("${adv.clerk.kafka.create.topic}")
	private String advClerkCreateTopic;

	@Value("${adv.clerk.kafka.update.topic}")
	private String advClerkUpdateTopic;

	@Value("${adv.clerk.idgen.id.name}")
	private String advClerkIdName;

	@Value("${adv.clerk.idgen.id.format}")
	private String advClerkIdFormat;

	// File store config
	@Value("${egov.fileStore.host}")
	private String fileStoreHost;

	@Value("${egov.fileStore.path}")
	private String fileStorePath;

	@Value("${moduleName}")
	private String moduleName;

	// MDMS
	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndPoint;

	// HRMS
	@Value("${egov.hrms.host}")
	private String hrmsHost;

	@Value("${egov.hrms.search.endpoint}")
	private String hrmsEndPoint;

	// URLShortening
	@Value("${egov.url.shortner.host}")
	private String urlShortnerHost;

	@Value("${egov.url.shortner.endpoint}")
	private String urlShortnerEndpoint;

	// SMSNotification
	@Value("${egov.sms.notification.topic}")
	private String smsNotificationTopic;
}
