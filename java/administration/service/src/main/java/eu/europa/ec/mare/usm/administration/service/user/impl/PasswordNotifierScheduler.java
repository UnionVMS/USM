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

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * J2EE scheduled singleton for triggering the sending of 
 * notifications (or reminders) to users whose password is 
 * about to expire.
 */
@Singleton
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class PasswordNotifierScheduler  {
  private static final Logger LOGGER = LoggerFactory.getLogger(PasswordNotifierScheduler.class);

  @EJB
  PasswordExpiryNotifier expiryReminder;
  
  /**
   * Creates a new instance.
   */
  public PasswordNotifierScheduler() 
  {
  }
  
  /**
   * Triggers the sending of notifications (or reminders) to users 
   * whose password is about to  expire.
   * 
   * Scheduled job running every 6 hours on the hour.
   */
  @Schedule(dayOfWeek="*", hour="*/6", minute="0")  
//  @Schedule(dayOfWeek="*", hour = "*", minute="*/5")  
  public void triggerNotifications() 
  {
    LOGGER.info("triggerNotifications() - (ENTER)");
    
    List<String> userNames = expiryReminder.findUsersToNotify();

    for (String userName : userNames) {
      expiryReminder.notifyUser(userName);
    }
    
    LOGGER.info("triggerNotifications() - (LEAVE)");
  }
    
}