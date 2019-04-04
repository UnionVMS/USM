The wildfly dockerfile allows preparing a wildfly server ready for arquillian
tests and pointing to the cygnus-test postgres db or a 
It includes a fakesmtp server that gets started at the beginning of the 
standalone.sh script.

Pre-requisites:
- docker-machine installed and running
- setting maven properties for docker.machine.ip and docker.certPath
	for example add the following profile to you maven settings and activate it (adapt as needed)
	 <profile>
      <id>dockermachine</id>
      <properties>
        <docker.machine.ip>192.168.99.100</docker.machine.ip>
        <docker.certPath>C:\\Users\\youruserhome\\.docker\\machine\\certs</docker.certPath>
        </properties>
    </profile>
- create a docker profile in your settings to override the value of the wf.host and endpoint-url property
  you will have to include this profile when running the tests
	example:
     <profile>
      <id>wf-docker</id>
      <properties>
        <wf.host>${docker.machine.ip}</wf.host>
        <endpoint-url>http://${wf.host}:${wf.port}/${webroot}/${restprefix}/</endpoint-url>
      </properties>
    </profile> 

Maven integration
-----------------

The docker-maven-plugin by fabric8.io is used to configure an image under the
name usm-pg-db that can get built and run using maven commands.

The goal to start docker is attached to the process-test-resources phase, while
the goals to stop and remove docker containers are attached to the clean phase.
The maven lifecycle management of the docker maven plugin goals is done through
the docker-run profile in the USM-parent pom and the docker.clean.phase and 
docker.start.phase properties. Two profiles in this project's pom allow
selecting a wildfly docker image with a datasource point to either cygnus-test
or to another container running postgres.


The arquillian tests are attached to the surefire plugin test phase.

This means it should be possible to get the docker container running separately
from running the tests, but it is also possible to everything done in one call.

Starting the wildfly docker container with the USM2 datasource pointing to 
cygnus-test db

mvn clean process-test-resources -Pdocker-run,docker-testdb

Stopping this container:

mvn clean -Pdocker-run,docker-testdb


Starting the wildfly docker container with the USM2 datasource pointing to a 
postgres container started from the usm liquibase pproject

mvn clean process-test-resources -Pdocker-run,docker-linkdb

Stopping this container:

mvn clean -Pdocker-run,docker-testdb

NOTE: if you have previsouly started one of these two possible containers and
then try to start the other, you may get an error if you do not stop the
running one first.

Run the arquillian tests
------------------------

Having set up a wf-docker profile in your maven settings as described above you
should be able to run the tests using

mvn clean test -Pwildfly,wf-docker

Or for running a single test

mvn clean test -Pwildfly,wf-docker -Dtest=ApplicationServiceTest.java

Combining it all:

mvn clean test -Pwildfly,wf-docker,docker-run,docker-linkdb

This will build the image and run the arquillian tests. Note that the container
will NOT be stopped at the end.


to build, run and test manually use the following steps:
- cd to the docker/wildfly folder
- build the image: $ docker build --rm --tag wildfly-uvms .
- start the image: $ docker run -it --name wf8 -p 9990:9990 -p 8080:8080 -p 8787:8787 --rm wildfly-uvms --debug
- add separate docker profile to your settings to override the wf.host property without activating the docker profile
  example 
     <profile>
      <id>docker-wf</id>
      <properties>
        <wf.host>${docker.machine.ip}</wf.host>
        <endpoint-url>http://${wf.host}:${wf.port}/${webroot}/${restprefix}/</endpoint-url>
      </properties>
    </profile>
    
- run the verify phase optionally selecting a single test and maybe method
  $ mvn clean verify -Pwildfly,docker-wf -Dtest=ManageUserServiceTest.java#testResetPasswordAndNotify
  or for IT tests in rest service
  $ mvn clean verify -Pwildfly,dockerwf -Dit.test=OrganisationRestServiceIT
  
Debugging tests from eclipse
----------------------------
wildfly must have been started in debug mode and the port must be exposed (this
is achieved by passing the --debug argument and by including the -p 8787:8787 
option when staring the docker container) 
Open debug configurations (menu Run > Debug Configurations...)
select Java Remote Application type and click add button
set project, set host to the docker machine ip and port to wildfly debug port

Debug an actual arquillian test
	1. Select maven profiles on the project in eclipse (Ctrl+alt+P)
	2. Create or update arduillian.launch_file to contain:
widlfly-remote
	3. Add a break point in the sources
	4. Start the Remote Java Application configuration created previously
	5. Debug test 




  