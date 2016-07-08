For testing your JVM's cacerts truststore should be supplemented with the ApacheDS certificate.

From the command line:
- determine where your jre lives
    * on windows: for %i in (java.exe) do @echo.   %~$PATH:i
    * on linux: which java
    When running the arquillian tests on a remote wildfly container (the default) check in the wildfly config (http://localhost:9990/console/App.html#environment) to find out. 
    Also make sure to restart your server after performing the import below
- cd to your_jre\lib\security
- copy the cert to this folder
- use the following command
../../bin/keytool -importcert -alias somealias -file certfile -trustcacerts -keystore cacerts
(note that the default password for the cacerts keystore is changeit)
-verify it got imported:
../../bin/keytool -list -v -keystore cacerts -alias somealias