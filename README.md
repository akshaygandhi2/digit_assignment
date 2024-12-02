# Digit Assignment

This service handles the registration of Advocates and Advocate Clerks in the system. Advocates and Advocate clerks are created in the user and individual registries with linkages.

## Dependencies

- egov-idgen
- MDMS v2
- egov-user
- individual
- egov-workflow-v2
- egov-notification-sms

## Configuration
1. Update the application.properties file to adjust the database and Flyway configuration sections with the appropriate database name, username, and password.
2. Provide the host URLs for the dependent services:
- egov.individual.host
- egov.mdms.host
- egov.user.host
- egov.idgen.host
- egov.workflow.host
