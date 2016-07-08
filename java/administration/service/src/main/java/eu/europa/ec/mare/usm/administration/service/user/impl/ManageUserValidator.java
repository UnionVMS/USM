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
package eu.europa.ec.mare.usm.administration.service.user.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europa.ec.mare.usm.administration.domain.ChallengeInformation;
import eu.europa.ec.mare.usm.administration.domain.ChallengeInformationResponse;
import eu.europa.ec.mare.usm.administration.domain.ChangePassword;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.administration.domain.UserAccount;
import eu.europa.ec.mare.usm.administration.service.RequestValidator;


/**
 * Provides operations for the validation and authorisation of manage user 
 * related service requests
 */
public class ManageUserValidator extends RequestValidator {

  
  /**
	 * Creates new instance
	 */
	public ManageUserValidator()
  {
	}

  /**
   * Asserts that the provided service request is valid and optionally, 
   * that the service requester is allowed to use the specified feature.
   * 
   * @param request the service request to be validated
   * @param feature the optional feature to which the service requester must
   * have been granted a right to use
   * 
   * @throws IllegalArgumentException if the service request is null, empty or 
   * incomplete 
   * @throws  UnauthorisedException if the service requester is not allowed
   * to use the specified feature
   */
	public void assertValidUser(ServiceRequest<UserAccount> request,
                                        USMFeature feature) 
  throws IllegalArgumentException, UnauthorisedException
  {
		assertValid(request, feature, "user");
    
    UserAccount user = request.getBody();

    assertNotEmpty("username", user.getUserName());
    assertNotTooLong("username", 32, user.getUserName());
    assertNotEmpty("firstName", user.getPerson().getFirstName());
    assertNotTooLong("firstName", 32, user.getPerson().getFirstName());
    assertNotEmpty("lastName", user.getPerson().getLastName());
    assertNotTooLong("lastName", 32, user.getPerson().getLastName());
    assertNotEmpty("email",user.getPerson().getEmail());
    assertNotTooLong("email",64, user.getPerson().getEmail());
    assertNotNull("organisation",user.getOrganisation());
    assertNotEmpty("organisationName",user.getOrganisation().getName());
    assertNotNull("activeFrom", user.getActiveFrom());
    assertNotNull("activeTo", user.getActiveTo());
    assertNotTooLong("Lockout reason", 128, user.getLockoutReason());
    
    assertValidPeriod("active", user.getActiveFrom(), user.getActiveTo());
	}
	
  /**
   * Asserts that the provided service request is valid and optionally, 
   * that the service requester is allowed to use the specified feature.
   * 
   * @param request the service request to be validated
   * @param feature the optional feature to which the service requester must
   * have been granted a right to use
   * 
   * @throws IllegalArgumentException if the service request is null, empty or 
   * incomplete 
   * @throws  UnauthorisedException if the service requester is not allowed
   * to use the specified feature
   */
  public void assertValidChangePassword(ServiceRequest<ChangePassword> request,
                                            USMFeature feature) 
  throws IllegalArgumentException, UnauthorisedException
  {
	  
		assertValid(request, feature, "changePassword");

		ChangePassword body = request.getBody();

		assertNotEmpty("changePassword.username", body.getUserName());
		assertNotTooLong("changePassword.username", 32, body.getUserName());
		assertNotEmpty("changePassword.newPassword", body.getNewPassword());
		assertNotTooLong("changePassword.newPassword", 32, body.getNewPassword());
  }
	
  
  public void assertValidChallengeUSer(ServiceRequest request, USMFeature feature, String userName) 
  throws IllegalArgumentException, UnauthorisedException
  {
    // Check who is using the feature?
    String requester = request.getRequester();
    if (!requester.equals(userName)) {
      throw new UnauthorisedException(UnauthorisedException.USER_UNAUTHORISED);
    }
  }
  
  /**
   * Asserts that the provided service request is valid and optionally, 
   * that the service requester is allowed to use the specified feature.
   * 
   * @param request the service request to be validated
   * @param feature the optional feature to which the service requester must
   * have been granted a right to use
   * 
   * @throws IllegalArgumentException if the service request is null, empty or 
   * incomplete 
   * @throws  UnauthorisedException if the service requester is not allowed
   * to use the specified feature
   */
  public void assertValidChallengeInformation(ServiceRequest<ChallengeInformationResponse> request, USMFeature feature) 
  throws IllegalArgumentException, UnauthorisedException
  {
    assertValid(request, feature, "setChallenges");

    ChallengeInformationResponse challengeInformationResponse = request.getBody();

    List<ChallengeInformation> challengeInformations = challengeInformationResponse.getResults();
    Map<String, ChallengeInformation> challengeMap = new HashMap<String, ChallengeInformation>();
    Map<String, ChallengeInformation> responseMap = new HashMap<String, ChallengeInformation>();

    for (int i = 1; i < challengeInformations.size() + 1; i++) {
      ChallengeInformation challengeInformation = challengeInformations.get(i - 1);

      assertNotEmpty("setChallenge.challenge" + i, challengeInformation.getChallenge());
      //assertNotTooLong("setChallenge.challenge" + i, 128, challengeInformation.getChallenge());

      assertNotEmpty("setChallenge.response" + i, challengeInformation.getResponse());
      //assertNotTooLong("setChallenge.response" + i, 128, challengeInformation.getResponse());
      //assertNotTooShort("setChallenge.response" + i, 3, challengeInformation.getResponse());
      
      if (challengeInformation.getChallenge() != null && challengeInformation.getChallenge().length() > 128)
    	  throw new IllegalArgumentException("Question "+i+"is too long. Please provide one that is at most "+128+" characters long.");
      
      if (challengeInformation.getResponse() != null && challengeInformation.getResponse().length() > 128)
    	  throw new IllegalArgumentException("Answer to question "+i+" is too long. Please provide one that is at most "+128+" characters long.");
      
      if (challengeInformation.getResponse() != null && challengeInformation.getResponse().length() < 3)
    	  throw new IllegalArgumentException("Answer to question "+i+" is too short. Please provide one that is at least "+3+" characters long.");

      
      
      if (challengeMap.containsKey(challengeInformation.getChallenge())) {
        throw new IllegalArgumentException("The same question cannot be used multiple times");
      } else {
        challengeMap.put(challengeInformation.getChallenge(), challengeInformation);
      }

      if (responseMap.containsKey(challengeInformation.getResponse())) {
        throw new IllegalArgumentException("The same answer cannot be used multiple times");
      } else {
        responseMap.put(challengeInformation.getResponse(), challengeInformation);
      }
    }
  }  
  
}