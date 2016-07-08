INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Authentication', 'ldap.enabled', 'false', 
'Flag that controls whether LDAP based authentication is enabled (value "true") or disabled (value "false"');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Authentication', 'ldap.server.url', 'ChangeMe', 
'URL for the LDAP server (e.g. ldaps://ldap.domain.org:636/)');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Authentication', 'ldap.context.root', 'ChangeMe', 
'Distinguished Name of the directory node under which users are searched for');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Authentication', 'ldap.bind.dn', 'ChangeMe', 
        'Distinguished Name of the LDAP user account used to query the directory');
 
INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Authentication', 'ldap.bind.password', 'ChangeMe', 
        'Password of the LDAP user account used to query the directory');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Authentication', 'ldap.query.filter', 'ChangeMe', 
        'LDAP query used to retrieve the distinguished name of a user, given the USM user name');

INSERT INTO POLICY_T (SUBJECT, NAME, VALUE, DESCRIPTION) 
VALUES ('Authentication', 'ldap.query.attributes', 'ChangeMe', 
        'Comma separated list of LDAP attributes to be retrieved from a user account');
