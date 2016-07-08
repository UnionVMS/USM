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
package eu.europa.ec.mare.usm.administration.service.userPreference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.mare.usm.administration.domain.FindUserPreferenceQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.Preference;
import eu.europa.ec.mare.usm.administration.domain.UserPreferenceResponse;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;

/**
 * Unit-test for the UserPreferenceService
 */
@RunWith(Arquillian.class)
public class UserPreferenceServiceTest extends DeploymentFactory {

	private static final String USM_ADMIN = "usm_admin";
	
	@EJB
	UserPreferenceService testSubject;
	
	/**
	 * Creates a new instance
	 */
	public UserPreferenceServiceTest() {
	}

	/**
	 * Tests the get user preferences method providing only user name in order to get
	 * all user's preferences
	 */
	@Test
	public void testGetUserPreferencesProvidingName() {

		// Setup
		FindUserPreferenceQuery query = new FindUserPreferenceQuery();
		query.setUserName("vms_super_fra");
		ServiceRequest<FindUserPreferenceQuery> sRequest = new ServiceRequest<>();
		sRequest.setRequester(USM_ADMIN);
		sRequest.setBody(query);
		String expected = "fr_FR";

		// Execute
		UserPreferenceResponse response = testSubject.getUserPrefernces(sRequest);

		// Verify
		assertNotNull("Unexpected null result", response);
		assertEquals("Unexpected 'option' value", expected,
				getOptionValue(response.getResults(), expected));

	}
	
	/**
	 * Tests the get user preferences method providing user and group name in order to get
	 * group filtered user's preferences
	 */
	@Test
	public void testGetUserPreferencesProvidingNameAndGroup() {

		// Setup
		FindUserPreferenceQuery query = new FindUserPreferenceQuery();
		query.setUserName("vms_super_fra");
		query.setGroupName("Union-VMS");
		ServiceRequest<FindUserPreferenceQuery> sRequest = new ServiceRequest<>();
		sRequest.setRequester(USM_ADMIN);
		sRequest.setBody(query);
		String expected = "fr_FR";
		String expectedGroup = "Union-VMS";

		// Execute
		UserPreferenceResponse response = testSubject.getUserPrefernces(sRequest);

		List<Preference> results = response.getResults(); 
		
		// Verify
		assertNotNull("Unexpected null result", response);
		assertEquals("Unexpected 'option' value", expected,
				getOptionValue(results, expected));
		assertEquals("Unexpected 'group name' value", expectedGroup,
				getGroupName(results, expectedGroup));

	}

	private String getOptionValue(List<Preference> userPreferences, String expected) {
        for (Preference userPreference : userPreferences) {
            if ((new String(userPreference.getOptionValue())).equals(expected)) {
                return expected;
            }
        }
        return null;
    }
	
	private String getGroupName(List<Preference> userPreferences, String expected) {
		if(userPreferences.size() > 0){
			// check only the first value as all elements should have the same group name value
			if (userPreferences.get(0).getGroupName().equals(expected)) {
                return expected;
            } else {
            	return null;
            }
		}
        return null;
    }

}