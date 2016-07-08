INSERT INTO USER_ROLE_T (ROLE_ID, USER_ID) 
select r.ROLE_ID, u.USER_ID
from APPLICATION_T a, USER_T u, ROLE_T r 
where a.NAME='USM'
  and u.USER_NAME='usm_bootstrap'
  and r.NAME='USM-UserManager'
  and r.APPLICATION_ID=a.APPLICATION_ID;

INSERT INTO USER_ROLE_T (ROLE_ID, USER_ID) 
select r.ROLE_ID, u.USER_ID
from APPLICATION_T a, USER_T u, ROLE_T r 
where a.NAME='USM'
  and u.USER_NAME='usm_bootstrap'
  and r.NAME='USM-UserBrowser'
  and r.APPLICATION_ID=a.APPLICATION_ID;

