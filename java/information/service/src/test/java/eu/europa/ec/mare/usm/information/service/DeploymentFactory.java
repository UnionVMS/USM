/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
package eu.europa.ec.mare.usm.information.service;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.deployment.Application;
import eu.europa.ec.mare.usm.information.entity.ApplicationEntity;
import eu.europa.ec.mare.usm.information.service.impl.InformationDao;
import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;

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