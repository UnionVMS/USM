INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'viewUsers', 'View the system users', 
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'manageUsers', 'Manage (create, update) the system users', 
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'activateUsers', 'Enable or disable the system users', 
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'viewOrganisations', 'View the system organisations and their end-points', 
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'manageOrganisations', 'Manage (create, update, delete) the system organisations and their end-points', 
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'viewApplications', 'View the system applications', 
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'manageApplications', 'Manage (create, update, delete) the system applications',
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'viewUserPreferences', 'View the system user preferences (or settings)', 
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'manageUserPreferences', 'Manage (create, update, delete) the system  user preferences (or settings)',
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'copyUserProfile', 'Copy roles, scopes and preferences from one user to another',
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'viewRoles', 'View the application roles',
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'manageRoles', 'Manage (create, update, delete) the application roles',
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'assignRoles', 'Assign or unassign application roles to/from users',
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'activateRoles', 'Enable or disable application roles',
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'viewScopes', 'View the application scopes',
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'manageScopes', 'Manage (create, update, delete) the application scopes',
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'assignScopes', 'Assign or unassign application scopes to/from users',
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'activateScopes', 'Enable or disable application scopes',
APPLICATION_ID from APPLICATION_T where NAME='USM';

INSERT INTO FEATURE_T (NAME, DESCRIPTION, APPLICATION_ID) 
select 'configurePolicies', 'Configure password, account, etc. policies',
APPLICATION_ID from APPLICATION_T where NAME='USM';

