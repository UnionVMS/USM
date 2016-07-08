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
package eu.europa.ec.mare.usm.session.service.impl;

import java.util.Date;
import java.util.List;

/**
 * Data-access object for the UserSessions 
 */
public interface SessionDao {

  /**
   * Creates (ore stores) the given user session.
   * 
   * @param session the user session to be created
   * 
   * @return the session unique identifier
   * 
   * @throws RuntimeException in case an internal error prevented processing 
   * the request
   */
  public String createSession(UserSession session)
  throws RuntimeException;

  /**
   * Reads the user session with the  provided unique identifier.
   * 
   * @param uniqueId the session unique identifier
   * 
   * @return the user session if it (still) exists, null otherwise
   * 
   * @throws RuntimeException in case an internal error prevented processing 
   * the request
   */
  public UserSession readSession(String uniqueId)
  throws RuntimeException;

  /**
   * Retrieves all existing sessions for the user with the provided
   * name, started after the provided date.
   * 
   * @param userName the user name
   * @param startedAfter the minimum session start date.
   * 
   * @return the possibly-empty list of user sessions matching the provided 
   * criteria
   * 
   * @throws RuntimeException in case an internal error prevented processing 
   * the request
   */
  public List<UserSession> readSessions(String userName, Date startedAfter)
  throws RuntimeException;

  /**
   * Deletes the user session with the provided unique identifier.
   * 
   * @param uniqueId the session unique identifier
   * 
   * @throws RuntimeException in case an internal error prevented processing 
   * the request
   */
  public void deleteSession(String uniqueId)
  throws RuntimeException;

  /**
   * Deletes all user sessions.
   * 
   * @throws RuntimeException in case an internal error prevented processing 
   * the request
   */
  public void deleteSessions()
  throws RuntimeException;
}