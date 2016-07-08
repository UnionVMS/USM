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
package eu.europa.ec.mare.usm.information.service.impl;

import java.util.List;

import javax.ejb.EJB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.service.InformationService;

/**
 * Provides operations for the validation and authorisation of service requests
 */
public class RequestValidator {
  protected static final Logger LOGGER = LoggerFactory.getLogger(RequestValidator.class);

  @EJB
  private InformationService infoService;
  
  
  /**
   * Creates a new instance.
   */
  public RequestValidator() {
  }

    public void assertNotTooLong(String name, int maxLen, String value) 
  {
    if (value != null && value.length() > maxLen) {
      throw new IllegalArgumentException(name + " is too long (max " + maxLen + ")");
    }
  }

  public void assertNotTooShort(String name, int minLen, String value) 
  {
    if (value != null && value.length() < minLen) {
      throw new IllegalArgumentException(name + " is too short (min " + minLen + ")");
    }
  }  
  
  public void assertNotEmpty(String name, String value) 
  {
    assertNotNull(name, value);
    if (value.trim().length() == 0) {
      throw new IllegalArgumentException(name + " must be defined");
    }
  }

  public void assertNotNull(String name, Object value) 
  {
    if (value == null) {
      throw new IllegalArgumentException(name + " must be defined");
    }
  }

  protected void assertInList(String name, String[] listOfValues, String value) 
  {
    if (value != null) {
      boolean inList = false;
      
      for (String v : listOfValues) {
        if (v.equals(value)) {
          inList = true;
          break;
        }
      }
      if (!inList) {
        throw new IllegalArgumentException(name + " (" + value + 
                                           ") is not supported");
      }
    }
  }

  protected void assertInList(String name, List<String> listOfValues, String value) 
  {
    if (value != null) {
      boolean inList = false;
      
      for (String v : listOfValues) {
        if (v.equals(value)) {
          inList = true;
          break;
        }
      }
      if (!inList) {
        throw new IllegalArgumentException(name + " (" + value + 
                                           ") is not supported");
      }
    }
  }

}