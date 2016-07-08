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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Computes hash of passwords
 */
public class PasswordDigester {
  private static final String MD5 = "MD5";

  
  /**
   * Computes the MD5 hash of the provided password.
   * 
   * @param password the password
   * 
   * @return the computed hash as an hexadecimal string
   * 
   * @throws RuntimeException in case the MD5 algorithm is not supported  
   */
  public String hashPassword(String password)
  {
    String ret = null;
    
    if (password != null) {
      try {
        MessageDigest md = MessageDigest.getInstance(MD5);
        md.update(password.getBytes());
        byte digest[] = md.digest();
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
          sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        ret = sb.toString();
      } catch (NoSuchAlgorithmException ex) {
        throw new RuntimeException("Failed to compute hash", ex);
      }
    }
    
    return ret;
  }
  
}