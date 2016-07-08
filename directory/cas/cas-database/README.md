CAS4 Overlay Template
============================

CAS maven war overlay that produces a CAS server instance that integrates with USM at the database level.
The integration was performed following instructions provided in:
https://jasig.github.io/cas/4.0.x/installation/Database-Authentication.html 

The integration was implemented in the deployerConfigContext.xml configuration file (see below) by defining the following three (Spring) beans:

* bean id="dataSource" defines a JDBC data-source connected to the USM database schema
* bean id="passwordEncoder" defines an MD5 password encoder (algorithm used by USM)
* bean id="dbAuthHandler" defines a CAS AuthenticationHandler that queries the USM ACTIVE_USER_V view to authenticate users

# Versions
```xml
<cas.version>4.0.2</cas.version>
```

# Recommended Requirements
* JDK 1.7+
* Apache Maven 3+
* Servlet container supporting Servlet 3+ spec (e.g. Apache Tomcat 7+)

# Configuration
The files that would need to be configured to satisfy local CAS installation needs are:

* `src/main/webapp/WEB-INF/deployerConfigContext.xml`
* `src/main/webapp/WEB-INF/cas.properties`
* `src/main/resources/log4j.xml`

# Deployment

## Maven
* Execute `mvn clean package`
* Deploy resultant `target/cas-database.war` to a Servlet container of choice.


