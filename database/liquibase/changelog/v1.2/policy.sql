INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.enabled', 'false', 
'Flag that controls whether LDAP based authentication is enabled for user administration (value "true") or disabled (value "false"');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.server.url', 'ChangeMe', 
'URL for the LDAP server for user administration (e.g. ldaps://ldap.domain.org:636/)');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.context.root', 'ChangeMe', 
'Distinguished Name of the directory node under which users are searched for when using LDAP in user administration');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.bind.dn', 'ChangeMe', 
        'Distinguished Name of the LDAP user account used to query the directory when using LDAP in user administration');
 
INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.bind.password', 'ChangeMe', 
        'Password of the LDAP user account used to query the directory');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.query.filter', 'ChangeMe', 
        'LDAP query used to retrieve the distinguished name of a user, given the USM user name when using LDAP in user administration');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.query.attributes', 'ChangeMe', 
        'Comma separated list of LDAP attributes to be retrieved from a user account when using LDAP in user administration');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.label.firstName', 'cn', 
        'The user attribute mapping to the firstName label on the UI when using LDAP in user administration');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.label.lastName', 'givenName', 
        'The user attribute mapping to the lastName label on the UI when using LDAP in user administration');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.label.telephoneNumber', 'telephoneNumber', 
        'The user attribute mapping to the telephoneNumber label on the UI when using LDAP in user administration');
        
INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.label.mobileNumber', 'mobile', 
        'The user attribute mapping to the mobileNumber label on the UI when using LDAP in user administration');
        
INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.label.faxNumber', 'facsimileTelephoneNumber', 
        'The user attribute mapping to the faxNumber label on the UI when using LDAP in user administration');
        
INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Administration', 'ldap.label.mail', 'mail', 
        'The user attribute mapping to the mail label on the UI when using LDAP in user administration');
