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
package eu.europa.ec.mare.usm.administration.service.user;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Unit test for the PasswordGenerator
 */
public class PasswordGeneratorTest {
  
  private static final int minLen = 8;
  private static final int maxLen = 32;
  private static final int noOfCAPSAlpha = 1;
  private static final int noOfAlpha = 1;
  private static final int noOfDigits = 1;
  private static final int noOfSplChars = 1;
  
  public PasswordGeneratorTest() 
  {
  }
  

  /**
   * Tests the generate method.
   */
  @Test
  public void testGenerate() 
  {
    generate();
  }

  /**
   * Tests the generate method, checking for duplicates.
   */
  @Test
  public void testGenerateCheckForDuplicates() 
  {
    Set<String> set = new HashSet<>();
    
    for (int i = 0; i < 50; i++) {
      String pwd = generate();
      if (set.contains(pwd)) {
        fail("Found duplicate: " + pwd);
      } else {
        set.add(pwd);
      }
    }
  }

  private String generate() 
  {
    String result = PasswordGenerator.generatePswd(minLen, maxLen, noOfCAPSAlpha, noOfAlpha, noOfDigits, noOfSplChars);
    assertNotNull("Unexpected null result", result);
    assertFalse("Unexpected empty result", result.isEmpty());
    
    System.out.println("Password : " + result);
    return result;
  }
  
}