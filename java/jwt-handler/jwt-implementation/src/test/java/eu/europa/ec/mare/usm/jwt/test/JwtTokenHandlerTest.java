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
package eu.europa.ec.mare.usm.jwt.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.jwt.DefaultJwtTokenHandler;
import eu.europa.ec.mare.usm.jwt.JwtTokenHandler;
import eu.europa.ec.mare.usm.jwt.jndiUtil;


/**
 * Unit-test for the ApplicationService
 */
@RunWith(Arquillian.class)
public class JwtTokenHandlerTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenHandlerTest.class);
  private static final String USER_NAME = "usm_user";
  private static final String RANDOM_SIG_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ1c20vYXV0aGVudGljYXRpb24iLCJpc3MiOiJ1c20iLCJzdWIiOiJhdXRoZW50aWNhdGlvbiIsImlhdCI6MTQ2MTA3NzUxMSwiZXhwIjoxNDYxMDc5MzExLCJ1c2VyTmFtZSI6InVzbV91c2VyIn0.QIn18uc09ajddT6ydLqMPO-P3IdmEa9L8e4s8Zck_YQ";
  

  @Deployment
  public static JavaArchive createDeployment() 
  {
    JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "ArquillianTest.jar")
            .addAsResource("jwt.properties")
            .addClass(JwtTokenHandler.class)
            .addClass(jndiUtil.class)
            .addClass(DefaultJwtTokenHandler.class)
            .addPackages(true,"io.jsonwebtoken")
            .addPackages(true,"com.fasterxml.jackson")
            .addPackages(true,"eu.europa.ec.mare.usm.jwt.test")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    return jar;
  }
  
  @EJB
  JwtTokenHandler testSubject;

  /**
   * Creates a new instance
   */
  public JwtTokenHandlerTest() {
  }

	/**
	 * Tests the getApplicationNames method
	 */
	@Test
	public void testCreateToken() {
	  
	  String token = testSubject.createToken(USER_NAME);
		// Verify
		assertNotNull("Unexpected null response", token);
	}
	
	@Test
	public void testParseToken() {
	  
	  String token = testSubject.createToken(USER_NAME);
	  String parsed = testSubject.parseToken(token);

	  // Verify
	  assertEquals(USER_NAME, parsed);
	}
	@Test
	public void testtamperedParseToken() {
	  

	  String parsed = testSubject.parseToken(RANDOM_SIG_TOKEN);
	  
	  // Verify
	  assertThat(parsed, is(not(USER_NAME)));
	  assertEquals(null, parsed);
	  
	}
	


}