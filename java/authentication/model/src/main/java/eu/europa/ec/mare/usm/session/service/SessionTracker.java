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
package eu.europa.ec.mare.usm.session.service;

import eu.europa.ec.mare.usm.session.domain.SessionInfo;

/**
 * Provides operations for monitoring user sessions; enforcing
 * the applicable policies (maximum number of sessions per user).
 */
public interface SessionTracker {

  /**
   * Informs the tracker that a (new) user session is starting.
   * 
   * @param sessionInfo the identification of the starting user session
   * 
   * @return the unique identifier of the starting user session
   * 
   * @throws IllegalStateException in case the creation of new session
   * violates the applicable policies
   * @throws IllegalArgumentException in case the provided input is null, 
   * empty or incomplete
   * @throws RuntimeException in case an internal error prevented processing 
   * the request
   */
  public String startSession(SessionInfo sessionInfo)
  throws IllegalStateException, IllegalArgumentException, RuntimeException;
  
  /**
   * Retrieves (identification) information about the user session 
   * with the provided identifier.
   * 
   * @param sessionId  the unique identifier of the user session
   * 
   * @return session identification information if the session (still) exists,
   * null otherwise
   * 
   * @throws IllegalArgumentException in case the provided input is null, 
   * empty or incomplete
   * @throws RuntimeException in case an internal error prevented processing 
   * the request
   */
  public SessionInfo getSession(String sessionId)
  throws IllegalArgumentException, RuntimeException;

  /**
   * Informs the tracker that a user session is terminating.
   * 
   * @param sessionId the unique identifier of the terminating user session
   * 
   * @throws IllegalArgumentException in case the provided input is null, 
   * empty or incomplete
   * @throws RuntimeException in case an internal error prevented processing 
   * the request
   */
  public void endSession(String sessionId)
  throws IllegalArgumentException, RuntimeException;
}