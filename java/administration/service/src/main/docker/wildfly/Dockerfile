FROM jboss/wildfly:8.2.1.Final

ENV ACTIVEMQ_VERSION 5.13.5

EXPOSE 9990 8080 8443

RUN /opt/jboss/wildfly/bin/add-user.sh wildfly wildfly123 --silent && \
	curl -o /opt/jboss/wildfly/standalone/deployments/activemq-rar.rar "https://repo1.maven.org/maven2/org/apache/activemq/activemq-rar/$ACTIVEMQ_VERSION/activemq-rar-$ACTIVEMQ_VERSION.rar"

RUN mkdir -p /opt/jboss/wildfly/modules/org/postgres/main
COPY drivers/postgresql-9.4.1208.jar /opt/jboss/wildfly/modules/org/postgres/main/
COPY drivers/module.xml /opt/jboss/wildfly/modules/org/postgres/main/

COPY fakesmtp /opt/jboss/fakesmtp

USER root
RUN mkdir -p /opt/jboss/fakesmtp/logs && \
	chown -R jboss:jboss /opt/jboss/fakesmtp && \
	chmod +x /opt/jboss/fakesmtp/fakesmtp.sh && \
	sed -i '2 a /opt/jboss/fakesmtp/fakesmtp.sh start' /opt/jboss/wildfly/bin/standalone.sh

USER jboss
COPY standalone-8.2-arquillian.xml /opt/jboss/wildfly/standalone/configuration/standalone-arquillian.xml

HEALTHCHECK --timeout=10s CMD /opt/jboss/wildfly/bin/jboss-cli.sh -c command=":read-attribute(name=server-state)" | awk 'NR==3 { print $3 }' | grep running

ENTRYPOINT ["/opt/jboss/wildfly/bin/standalone.sh","-c", "standalone-arquillian.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
CMD []