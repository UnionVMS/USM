For testing with ApacheDS deployed on midway your JVM's cacerts truststore should be supplemented with the ApacheDS certificate for midway.

From the command line:
- determine where your jre lives
    * on windows: for %i in (java.exe) do @echo.   %~$PATH:i
    * on linux: which java
    When running the arquillian tests on a remote wildfly container (the default) check in the wildfly config (http://localhost:9990/console/App.html#environment) to find out. 
    In weblogic check in Environment>Servers>adminServer then Configuration tab >Keystores tab to find the value of "Java Standard Trust Keystore"
    Also make sure to restart your server after performing the import below
- cd to your_jre\lib\security
- copy the svm-midway.athens.intrasoft-intl.private cert to this folder
- use the following command
../../bin/keytool -importcert -alias svm-midway.athens.intrasoft-intl.private -file svm-midway.athens.intrasoft-intl.private -trustcacerts -keystore cacerts
(note that the default password for the cacerts keystore is changeit)
-verify it got imported:
../../bin/keytool -list -v -keystore cacerts -alias svm-midway.athens.intrasoft-intl.private

This readme has also been setup as page on confluence. If changing information here please make sure to update the corresponding page: https://webgate.ec.europa.eu/CITnet/confluence/display/UNIONVMS/Set+up+truststore+for+ldap+on+midway