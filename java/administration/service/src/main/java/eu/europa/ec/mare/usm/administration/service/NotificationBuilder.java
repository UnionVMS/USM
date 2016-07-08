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
package eu.europa.ec.mare.usm.administration.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.Notification;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Builds Notification messages
 */
public class NotificationBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationBuilder.class);
  private static final String PROPERTIES = "/notification.properties";

  /**
   * Creates a new instance.
   */
  public NotificationBuilder() {
  }
  
  /**
   * Builds the notification message
   * 
   * @param recipient the recipient of the email
   * @param password the password that will be contained in the email
   * @return Notification the notification message
   */
  public static Notification buildNotification(String recipient, String password)
  {
	  Notification message = new Notification();
	  Properties prop = getProperties();
	  message.setSender(prop.getProperty("notification.sender"));
	  message.setRecipient(recipient);
	  message.setSubject(prop.getProperty("notification.subject"));
	  String content = MessageFormat.format(prop.getProperty("notification.content"), password);
	  message.setContent(content);
	  return message;    
  }
  
  /**
   * Builds a notification message that reminds a user about the 
   * pending expiry of her/his password.
   * 
   * @param email the user e-mail address
   * @param userName the user name 
   * @param passwordExpiry the password expiry date/time
   * 
   * @return a notification message
   */
  public static Notification buildReminder(String email, String userName, 
                                             Date passwordExpiry) 
  {
    LOGGER.debug("buildReminder(" + email + ", " + userName + ", " + 
                 passwordExpiry +") - (ENTER)");
    
	  Properties prop = getProperties();
    
    SimpleDateFormat sdf = new SimpleDateFormat(prop.getProperty("reminder.dateFormat"));
    String expiryDate = sdf.format(passwordExpiry);
    
	  Notification ret = new Notification();
	  ret.setSender(prop.getProperty("reminder.sender"));
	  ret.setRecipient(email);
	  ret.setSubject(prop.getProperty("reminder.subject"));
	  String content = MessageFormat.format(prop.getProperty("reminder.content"), 
                                          userName, expiryDate);
	  ret.setContent(content);

    LOGGER.debug("buildReminder() - (LEAVE): " + ret);
    return ret;    
  }

  
  private static Properties getProperties() 
  {
    Properties ret = new Properties();

    InputStream is = NotificationBuilder.class.getResourceAsStream(PROPERTIES); 	
    try {
      if (is == null) {
        throw new IOException("Resource " + PROPERTIES + " not found");
      } else {
        ret.load(is);
      }
    } catch (IOException exc) {
      LOGGER.error("Failed to load " + PROPERTIES, exc);
    }

    return ret;
  }

}