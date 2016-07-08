# PostgreSQL docker image based on Alpine Linux

This docker image builds on the 
[kiasaki/alpine-postgres](https://hub.docker.com/r/kiasaki/alpine-postgres/)
image that accepts the same env vars as the 
[official postgres build](https://registry.hub.docker.com/_/postgres/) but
with a much smaller footprint. It achieves that by basing itself off the tiny
official alpine linux image.

## Why?

```bash
$ docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             VIRTUAL SIZE
alpine-postgres     latest              82d0ddb748fd        About an hour ago   27.35 MB
alpine              latest              70c557e50ed6        5 weeks ago         4.798 MB
postgres            latest              6d6a71f8528e        4 weeks ago         265.1 MB
```

# Direct Usage

This image works in the same way the official `postgres` docker image work.

It's documented on DockerHub in it's README: [https://hub.docker.com/_/postgres/](https://hub.docker.com/_/postgres/).

For example, you can start a basic PostgreSQL server, protected by a password,
listening on port 5432 by running the following:

```
$ docker run --name some-postgres -e POSTGRES_PASSWORD=mysecretpassword -d kiasaki/alpine-postgres
```

Next, you can start you app's container while **linking** it to the PostgreSQL
container you just created giving it access to it.

```
$ docker run --name some-app --link some-postgres:postgres -d application-that-uses-postgres
```

Your app will now be able to access `POSTGRES_PORT_5432_TCP_ADDR` and `POSTGRES_PORT_5432_TCP_PORT` environment variables.

# Maven Integration for USM

In this project we are NOT using the built-in functionality of the postgres
image to look for an init script (although a sample is provided in the files
folder) but rely on the setting of environment variables `POSTGRES_USER`
and `POSTGRES_PASSWORD` to setup a postgres db with a `usm2` user and
`password` password.

The docker-maven-plugin by fabric8.io is used to configure an image under the
name usm-pg-db that can get built and run using maven commands.

The goal to start docker is attached to the process-test-resources phase, while
the goals to stop and remove docker containers are attached to the clean phase.

The liquibase maven plugin goal to update is attached to the test phase and the
sql plugin execution to clean the db is attached to the clean phase (but only 
within a specific DB profile, meaning that without specifying the db profile no
liquibase related tasks are executed).

## Installing the schema on docker

To install the schema on docker image with postgres from scratch you can run

`mvn test -Ppostgres,testdata,docker,docker-run`

The docker-run profile will build the image and start the container. It does
not stop the container and exposes the db on port 5432. You should now be able
to connect to the db using the pgadmin tool pointing at your docker machine ip.
You can also configure a datasource on a local server to point to this.

Once started you can perform a liquibase update without stopping the container
by using: 

`mvn test -Ppostgres,docker,testdata`

The docker profile is the one that sets the db connection properties needed by
the liquibase update goal. The testdata profile is the one that ensure the test
context changesets get run. 

In the same spirit you may clean the schema without stopping the container
To drop all objects from the schema use

`mvn clean -Ppostgres,docker`

## Managing the container

To stop and remove the docker container run
`mvn clean -Pdocker-run`

## Linking to this container

The maven configuration of the usm-pg-db image includes setting the hostname to
cygnus-test.private. This means that is possible for a
separate application server container to link to this container and use this db
in place of the cygnus-test configured datasource. See the USM administration
service.


To be completed:
Building the docker image manually
Running the docker image manually
Starting a shell (for debugging docker) instead of postgres
Logging docker image building and running

 


