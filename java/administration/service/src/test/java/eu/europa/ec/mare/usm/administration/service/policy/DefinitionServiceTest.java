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
package eu.europa.ec.mare.usm.administration.service.policy;

import eu.europa.ec.mare.usm.administration.domain.PolicyDefinition;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import javax.ejb.EJB;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 * Unit test for the EJB implementation of the DefinitionService
 */
@RunWith(Arquillian.class)
public class DefinitionServiceTest extends DeploymentFactory {
  private static final String PASSWORD_SUBJECT = "Password";
  private static final String MIN_LENGTH_PROPERTY = "password.minLnegth";
  private static final String MIN_SPECIAL_PROPERTY = "password.minSpecial";
  private static final String MIN_DIGITS_PROPERTY = "password.minDigits";

  @EJB
  DefinitionService testSubject;
  
  public DefinitionServiceTest() {
  }
  
  /**
   * Tests the getDefinition method.
   */
  @Test
  public void testGetDefinition() 
  {
    String subject = PASSWORD_SUBJECT;

    // Execute
    PolicyDefinition result = testSubject.getDefinition(subject);

    // Verify
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected subject", subject, result.getSubject());
    assertNotNull("Unexpected null properties", result.getProperties());
  }

  /**
   * Tests the setDefinition method.
   */
  @Test
  public void testSetDefinition() 
  {
    // Set-up
    String subject = PASSWORD_SUBJECT;
    PolicyDefinition setup = testSubject.getDefinition(subject);

    // Execute
    ServiceRequest<PolicyDefinition> request = new ServiceRequest<>();
    request.setRequester("usm_admin");
    request.setBody(setup);
    request.getBody().getProperties().put(MIN_DIGITS_PROPERTY, "2");
    request.getBody().getProperties().put(MIN_SPECIAL_PROPERTY, "1");
    testSubject.setDefinition(request);
    
    // Verify
    PolicyDefinition result = testSubject.getDefinition(subject);
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected subject", subject, result.getSubject());
    assertNotNull("Unexpected null properties", result.getProperties());

    assertEquals("Unexpected property value", 
                 request.getBody().getProperties().getProperty(MIN_DIGITS_PROPERTY), 
                 result.getProperties().getProperty(MIN_DIGITS_PROPERTY));
    assertEquals("Unexpected property value", 
                 request.getBody().getProperties().getProperty(MIN_SPECIAL_PROPERTY), 
                 result.getProperties().getProperty(MIN_SPECIAL_PROPERTY));
    assertEquals("Unexpected property value", 
                 request.getBody().getProperties().getProperty(MIN_LENGTH_PROPERTY), 
                 result.getProperties().getProperty(MIN_LENGTH_PROPERTY));
    
  }
  
}