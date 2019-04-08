FROM dgmare-repo.athens.intrasoft-intl.private:5000/jboss/wildfly

EXPOSE 9990 8080 8443

RUN /opt/jboss/wildfly/bin/add-user.sh wildfly wildfly123 --silent

RUN mkdir -p /opt/jboss/wildfly/modules/org/postgres/main
COPY drivers/postgresql-9.4.1208.jar /opt/jboss/wildfly/modules/org/postgres/main/
COPY drivers/module.xml /opt/jboss/wildfly/modules/org/postgres/main/

COPY fakesmtp /opt/jboss/fakesmtp

USER root
RUN chown -R jboss:jboss /opt/jboss/fakesmtp && \
	chmod +x /opt/jboss/fakesmtp/fakesmtp.sh && \
	sed -i '2 a /opt/jboss/fakesmtp/fakesmtp.sh start' /opt/jboss/wildfly/bin/standalone.sh

USER jboss
COPY standalone-arquillian.xml /opt/jboss/wildfly/standalone/configuration/

ENTRYPOINT ["/opt/jboss/wildfly/bin/standalone.sh","-c", "standalone-arquillian.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
CMD []