INSERT INTO PERMISSION_T (ROLE_ID, FEATURE_ID) 
select r.ROLE_ID, f.FEATURE_ID
from APPLICATION_T a, FEATURE_T f, ROLE_T r 
where a.NAME='USM'
  and f.NAME in ('manageUsers', 'activateUsers', 'manageOrganisations', 
                 'manageApplications', 'manageUserPreferences', 'copyUserProfile', 
                 'manageRoles', 'assignRoles', 'activateRoles', 'manageScopes', 
                 'assignScopes', 'activateScopes', 'configurePolicies','manageContacts')
  and r.NAME='USM-UserManager'
  and f.APPLICATION_ID=a.APPLICATION_ID
  and r.APPLICATION_ID=a.APPLICATION_ID;

INSERT INTO PERMISSION_T (ROLE_ID, FEATURE_ID) 
select r.ROLE_ID, f.FEATURE_ID
from APPLICATION_T a, FEATURE_T f, ROLE_T r 
where a.NAME='USM'
  and f.NAME in ('viewUsers', 'viewOrganisations', 'viewApplications', 
                 'viewUserPreferences', 'viewRoles', 'viewScopes', 'viewContacts')
  and r.NAME='USM-UserBrowser'
  and f.APPLICATION_ID=a.APPLICATION_ID
  and r.APPLICATION_ID=a.APPLICATION_ID;
