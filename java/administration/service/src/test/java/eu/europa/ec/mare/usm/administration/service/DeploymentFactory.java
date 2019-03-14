package eu.europa.ec.mare.usm.administration.service;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

//import eu.europa.ec.fisheries.schema.audit.common.v1.ExceptionType;
//import eu.europa.ec.fisheries.schema.audit.search.v1.AuditLogListQuery;
//import eu.europa.ec.fisheries.schema.audit.source.v1.CreateAuditLogRequest;
//import eu.europa.ec.fisheries.schema.audit.v1.AuditLogType;
//import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
//import eu.europa.ec.fisheries.uvms.audit.model.mapper.AuditLogMapper;
//import eu.europa.ec.mare.audit.logger.AuditLogger;
//import eu.europa.ec.mare.audit.logger.impl.LoggerFactoryBridge;
import eu.europa.ec.mare.usm.administration.common.DateParser;
import eu.europa.ec.mare.usm.administration.common.jdbc.BaseJdbcDao;
import eu.europa.ec.mare.usm.administration.domain.FindUsersQuery;
import eu.europa.ec.mare.usm.administration.service.application.ApplicationService;
import eu.europa.ec.mare.usm.administration.service.application.impl.ApplicationServiceBean;
import eu.europa.ec.mare.usm.administration.service.ldap.LdapUserInfoService;
import eu.europa.ec.mare.usm.administration.service.ldap.impl.LDAP;
import eu.europa.ec.mare.usm.administration.service.ldap.impl.LdapUserInfoServiceBean;
import eu.europa.ec.mare.usm.administration.service.organisation.OrganisationService;
import eu.europa.ec.mare.usm.administration.service.organisation.impl.OrganisationServiceBean;
import eu.europa.ec.mare.usm.administration.service.person.PersonService;
import eu.europa.ec.mare.usm.administration.service.person.impl.PersonServiceBean;
import eu.europa.ec.mare.usm.administration.service.policy.DefinitionService;
import eu.europa.ec.mare.usm.administration.service.policy.impl.PolicyValidator;
import eu.europa.ec.mare.usm.administration.service.role.RoleService;
import eu.europa.ec.mare.usm.administration.service.role.impl.RoleServiceBean;
import eu.europa.ec.mare.usm.administration.service.scope.ScopeService;
import eu.europa.ec.mare.usm.administration.service.scope.impl.ScopeServiceBean;
import eu.europa.ec.mare.usm.administration.service.user.ViewUsersService;
import eu.europa.ec.mare.usm.administration.service.user.impl.UserJdbcDao;
import eu.europa.ec.mare.usm.administration.service.user.impl.UserJpaDao;
import eu.europa.ec.mare.usm.administration.service.userContext.UserContextService;
import eu.europa.ec.mare.usm.administration.service.userContext.impl.UserContextServiceBean;
import eu.europa.ec.mare.usm.administration.service.userPreference.UserPreferenceService;
import eu.europa.ec.mare.usm.administration.service.userPreference.impl.UserPreferenceServiceBean;

/**
 *
 */
@ArquillianSuiteDeployment
public class DeploymentFactory {
  private static final String AUDIT_GROUP_ID = "eu.europa.ec.mare.auditing";
  private static final String USM_GROUP_ID = "eu.europa.ec.mare.usm";
  private static final String UVMS_AUDIT_GROUP_ID = "eu.europa.ec.fisheries.uvms.audit";

  @Deployment
  public static WebArchive createDeployment() {
    WebArchive war = ShrinkWrap.create(WebArchive.class, "ArquillianTest.war")
        // .addPackage(OrganisationEntity.class.getPackage())
        .addPackage(OrganisationService.class.getPackage())
        .addPackage(OrganisationServiceBean.class.getPackage())
        .addPackage(RoleService.class.getPackage())
        .addPackage(RoleServiceBean.class.getPackage())
        .addPackage(UserContextService.class.getPackage())
        .addPackage(UserContextServiceBean.class.getPackage())
        .addPackage(ScopeService.class.getPackage())
        .addPackage(ScopeServiceBean.class.getPackage())
        .addPackage(ApplicationService.class.getPackage())
        .addPackage(ApplicationServiceBean.class.getPackage())
        .addPackage(ViewUsersService.class.getPackage())
        .addPackage(UserJdbcDao.class.getPackage())
        .addPackage(FindUsersQuery.class.getPackage())
        // .addPackage(InformationDao.class.getPackage())
        // .addPackage(InformationService.class.getPackage())
        // .addPackage(UserContextQuery.class.getPackage())
        // .addPackage(AuthenticationResponse.class.getPackage())
        // .addPackage(AuthenticationServiceBean.class.getPackage())
        .addPackage(LDAP.class.getPackage())
        // .addPackage(AbstractJdbcDao.class.getPackage())
        // .addPackage(ChallengeResponse.class.getPackage())
        // .addPackage(SessionInfo.class.getPackage())
        // .addPackage(eu.europa.ec.mare.usm.service.impl.RequestValidator.class.getPackage())
        // .addPackage(PolicyProvider.class.getPackage())
        // .addPackage(AuthenticationDao.class.getPackage())
        // .addPackage(AuthenticationService.class.getPackage())
        .addClass(PolicyValidator.class)
        .addPackage(UserJpaDao.class.getPackage())
        .addPackage(BaseJdbcDao.class.getPackage())
        .addPackage(DateParser.class.getPackage())
        .addPackages(true, DefinitionService.class.getPackage())
        .addPackage(LdapUserInfoService.class.getPackage())
        .addPackage(LdapUserInfoServiceBean.class.getPackage())
        .addPackage(PersonService.class.getPackage())
        .addPackage(PersonServiceBean.class.getPackage())
        .addPackage(UserPreferenceService.class.getPackage())
        .addPackage(UserPreferenceServiceBean.class.getPackage())
        .addPackage(DeploymentFactory.class.getPackage())
        // .addPackage(AuditModelMarshallException.class.getPackage())
        // .addPackage(AuditLogMapper.class.getPackage())
        // .addPackage(CreateAuditLogRequest.class.getPackage())
        // .addPackage(AuditLogType.class.getPackage())
        // .addPackage(AuditLogListQuery.class.getPackage())
        // .addPackage(ExceptionType.class.getPackage())
        .addAsLibraries("Logger-API.jar", "CSV-Logger.jar",
            "Information-Service.jar", "Authentication-Service.jar",
            "Information-Model.jar", "Authentication-Model.jar",
            "audit-model.jar")

        /*
         * .addAsLibraries(Maven.configureResolver()
         * .workOffline(false).configureViaPlugin() .resolve(AUDIT_GROUP_ID +
         * ":Logger-API", AUDIT_GROUP_ID + ":CSV-Logger",
         * USM_GROUP_ID+":Information-Service",
         * USM_GROUP_ID+":Authentication-Service",
         * UVMS_AUDIT_GROUP_ID+":audit-model") .withTransitivity() .asFile())
         */

        // .addPackage("eu.europa.ec.mare.usm.information.domain.deployment")
        // .addPackage("eu.europa.ec.mare.usm.session.service.impl")
        // .addPackage("eu.europa.ec.mare.usm.session.service")
        // .addAsResource("META-INF/persistence.xml")

        .addAsResource("logback-test.xml").addAsResource("ApacheDS.properties")
        .addAsResource("password.properties")
        .addAsResource("notification.properties")
        .addAsWebInfResource("META-INF/beans.xml", "beans.xml")
    // .addAsManifestResource("META-INF/beans.xml", "beans.xml")
    // .addAsManifestResource("META-INF/ejb-jar.xml", "ejb-jar.xml")
    ;
    return war;
  }

  public DeploymentFactory() {
  }

}
