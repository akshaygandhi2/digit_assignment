CREATE TABLE eg_advocate_registration(
  id character varying(36),
  tenant_id character varying(128),
  application_number character varying(64),
  bar_registration_number character varying(64),
  advocate_type character varying(64),
  organisation_id character varying, 
  individual_id character varying,
  is_active boolean default true,
  additional_details JSONB,
  created_by character varying(64),
  last_modified_by character varying(64),
  created_time bigint,
  last_modified_time bigint,
 CONSTRAINT pk_eg_advocate_registration PRIMARY KEY (id)
);

CREATE TABLE eg_advocate_clerk_registration(
  id character varying(36),
  tenant_id character varying(128),
  application_number character varying(64),
  state_regn_number character varying(64),
  individual_id character varying,
  is_active boolean default true,
  additional_details JSONB,
  created_by character varying(64),
  last_modified_by character varying(64),
  created_time bigint,
  last_modified_time bigint,
 CONSTRAINT pk_eg_advocate_clerk_registration PRIMARY KEY (id)
);

CREATE TABLE eg_adv_document(
    id character varying(64),
    tenant_id character varying(64),
    document_type character varying(64),
    file_store character varying(64),
    document_uid character varying(64),
    additional_details JSONB,
    advocate_id character varying(36),
    advocate_clerk_id character varying(36),
    created_by character varying(64),
    last_modified_by character varying(64),
    created_time bigint,
    last_modified_time bigint,

    CONSTRAINT pk_eg_adv_document PRIMARY KEY (id),
    CONSTRAINT fk_eg_adv_document FOREIGN KEY (advocate_id) REFERENCES eg_advocate_registration (id),
    CONSTRAINT fk_eg_adv_clerk_document FOREIGN KEY (advocate_clerk_id) REFERENCES eg_advocate_clerk_registration (id)
);