------ Drop sequences -------
drop sequence SQ_ORGANISATION;
drop sequence SQ_PERSON;
drop sequence SQ_END_POINT;
drop sequence SQ_APPLICATION;
drop sequence SQ_FEATURE;
drop sequence SQ_OPTION;
drop sequence SQ_DATASET;
drop sequence SQ_POLICY;
drop sequence SQ_ROLE;
drop sequence SQ_SCOPE;
drop sequence SQ_USER;
drop sequence SQ_USER_ROLE;
drop sequence SQ_PREFERENCE;
drop sequence SQ_CHALLENGE;
drop sequence SQ_PASSWORD_HIST;
drop sequence SQ_CHANNEL;
drop sequence SQ_END_POINT_CONTACT;
drop sequence SQ_PENDING_DETAILS;

------Drop views ----
drop view ACTIVE_USER_V;
drop view ACTIVE_USER_SCOPE_V;
drop view ACTIVE_USER_FEATURE_RESOURCE_V;
drop view ACTIVE_USER_FEATURE_V;
drop view ACTIVE_USER_ROLE_V;
drop view USER_PROFILE_V;

------Drop tables ----
drop table CHANNEL_T;
drop table END_POINT_CONTACT_T;
drop table END_POINT_T;
drop table PREFERENCE_T;
drop table USER_ROLE_T;
drop table USER_CONTEXT_T;
drop table CHALLENGE_T;
drop table PASSWORD_HIST_T;
drop table USER_T;
drop table ORGANISATION_T;
drop table PERSON_T;
drop table PERMISSION_T;
drop table ROLE_T;
drop table SCOPE_DATASET_T;
drop table SCOPE_T;
drop table FEATURE_T;
drop table OPTION_T;
drop table DATASET_T;
drop table APPLICATION_T;
drop table POLICY_T;
drop table PENDING_DETAILS_T;

--drop liquibase tables
drop table databasechangelog;	
drop table databasechangeloglock;

-- purge recyclebin;
