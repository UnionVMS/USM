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
package eu.europa.ec.mare.usm.administration.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateParser {
  private static final String DATE_TIME_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
  private static final String DATE_FORMAT = "yyyy-MM-dd";

  public static Date parseDate(String name, String date) 
  {
    Date ret = null;

    if (date != null && !date.trim().isEmpty()) {
      SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_ZONE_FORMAT);
      try {
        ret = sdf.parse(date);
      } catch (ParseException ex1) {
        SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_TIME_FORMAT);
        try {
          ret = sdf2.parse(date);
        } catch (ParseException ex2) {
          SimpleDateFormat sdf3 = new SimpleDateFormat(DATE_FORMAT);
          try {
            ret = sdf3.parse(date);
          } catch (ParseException ex3) {
            throw new IllegalArgumentException("Unparsable " + name + " date: "
                    + ex3.getMessage());
          }
        }
      }
    }

    return ret;
  }

}