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
package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Service request for resetting password 
 */
public class ResetPasswordQuery implements Serializable {
  private static final long serialVersionUID = 1L;
  private String userName;
  private List<ChallengeInformation> challenges;
  private String password;
  private boolean isTemporaryPassword;

  /**
   * Creates a new instance.
   */
  public ResetPasswordQuery() {
  }

  /**
   * Get the value of userName
   *
   * @return the value of userName
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Set the value of userName
   *
   * @param userName new value of userName
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  /**
   * Get the value of challenges
   *
   * @return the value of challenges
   */
  public List<ChallengeInformation> getChallenges() {
		return challenges;
	}

  /**
   * Set the value of challenges
   *
   * @param challenges new value of challenges
   */
	public void setChallenges(List<ChallengeInformation> challenges) {
		this.challenges = challenges;
	}
	/**
	   * Get the value of password
	   *
	   * @return the value of password
	   */
	public String getPassword() {
		return password;
	}

	/**
	   * Set the value of password
	   *
	   * @param password new value of password
	   */
	public void setPassword(String password) {
		this.password = password;
	}

	
	
  public boolean isTemporaryPassword() {
		return isTemporaryPassword;
	}

	public void setIsTemporaryPassword(boolean isTemporaryPassword) {
		this.isTemporaryPassword = isTemporaryPassword;
	}

/**
   * Formats a human-readable view of this instance.
   *
   * @return a human-readable view
   */
  @Override
  public String toString() {
    return "NotificationQuery{"
            + "userName=" + userName
            + ",challenges=" + challenges
            + '}';
  }

}