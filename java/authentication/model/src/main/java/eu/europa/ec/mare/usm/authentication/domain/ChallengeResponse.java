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
package eu.europa.ec.mare.usm.authentication.domain;

/**
 * A challenge/response based authentication request.
 */
public class ChallengeResponse extends AuthenticationQuery {
	private static final long serialVersionUID = 1L;

	private String challenge;
	private String response;

  /**
   * Creates a new instance.
   */
  public ChallengeResponse() {
  }

  
  /**
   * Get the value of challenge
   *
   * @return the value of challenge
   */
  public String getChallenge() {
    return challenge;
  }

  /**
   * Set the value of challenge
   *
   * @param challenge new value of challenge
   */
  public void setChallenge(String challenge) {
    this.challenge = challenge;
  }


  /**
   * Get the value of response
   *
   * @return the value of response
   */
  public String getResponse() {
    return response;
  }

  /**
   * Set the value of response
   *
   * @param response new value of response
   */
  public void setResponse(String response) {
    this.response = response;
  }

  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "ChallengeResponse{" + 
            "userName=" + getUserName() + 
            "challenge=" + challenge + 
            ", response=" + (response == null ? null : "******") + 
            '}';
  }

}