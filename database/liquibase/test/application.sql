--liquibase formatted sql

--changeset emkrogean:1 context:test
INSERT INTO APPLICATION_T (NAME, DESCRIPTION) 
VALUES ('Quota', 'Quota Management application');
--rollback delete from APPLICATION_T where NAME = 'Quota';

--changeset emkrogean:2 context:test
INSERT INTO APPLICATION_T (NAME, DESCRIPTION) 
VALUES ('Union-VMS', 'Union VMS application');
--rollback delete from APPLICATION_T where NAME = 'Union-VMS';