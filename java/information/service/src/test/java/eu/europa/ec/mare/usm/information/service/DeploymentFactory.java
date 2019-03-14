package eu.europa.ec.mare.usm.information.service;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.deployment.Application;
import eu.europa.ec.mare.usm.information.entity.ApplicationEntity;
import eu.europa.ec.mare.usm.information.service.impl.InformationDao;
import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;
import eu.europa.ec.mare.usm.session.domain.SessionInfo;

/**
 *
 */
@ArquillianSuiteDeployment
public class DeploymentFactory {

  @Deployment
  public static JavaArchive createDeployment() 
  {
    JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "ArquillianTest.jar")
    		.addPackage("eu.europa.ec.mare.usm.policy.service.impl")
    		.addPackage("eu.europa.ec.mare.usm.service.impl")
    		.addPackage(AuthenticationRequest.class.getPackage())
    		.addPackage(SessionInfo.class.getPackage())
            .addPackage(InformationService.class.getPackage())
            .addPackage(InformationDao.class.getPackage())
            .addPackage(UserContext.class.getPackage())
            .addPackage(ApplicationEntity.class.getPackage())
            .addPackage(DeploymentFactory.class.getPackage())
            .addPackage("eu.europa.ec.mare.usm.information.domain.deployment")
            .addAsResource("META-INF/persistence.xml")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    return jar;
  }

  public DeploymentFactory() {
  }
  
}
