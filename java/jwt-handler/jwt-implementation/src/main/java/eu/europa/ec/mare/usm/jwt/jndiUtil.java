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
package eu.europa.ec.mare.usm.jwt;


import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.Queue;

import javax.naming.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class jndiUtil {

  private static InitialContext ic = null;
  private static final Logger LOGGER = LoggerFactory.getLogger(jndiUtil.class);

  public static void createJNDI(String JNDIName, String Value) {

    try {
      ic = new InitialContext();

      bindParam(ic, JNDIName, Value);
      try {
        ic.close();        
      } catch (Exception e) {
        // ignore close error
      }
     
    } catch (Exception e) {
       //try to get details and cause details
       LOGGER.warn("Error Binding "+ Value +" to "+JNDIName);
       LOGGER.warn(e.getMessage());
       if(e.getCause() != null){
	   LOGGER.debug(e.getCause().getMessage());
	   if(e.getCause().getCause().getClass().getName().equals("org.jboss.msc.service.DuplicateServiceException")){
	       LOGGER.error("Your jboss JNDI context already has a key bound. Your may have a secret key mismatch. Enable Trace for additional details (like the actual registered key)");
	       try {
		   LOGGER.trace("You tried binding " + Value + "but the currently registered key is "+lookup(JNDIName));}
	       catch (Exception ex) {
		// TODO: handle exception
	    }
	   } 
	   
       }
    }

  }
  
  public static Object lookup(String JNDIName){
    Object value = null;
    try {
      ic = new InitialContext();

      value = ic.lookup(JNDIName);
      try {
        ic.close();        
      } catch (Exception e) {
        // ignore close error
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return value;
  }
  
  public static Object unbind(String JNDIName){
    Object value = null;
    try {
      ic = new InitialContext();
      
      value = ic.lookup(JNDIName);
      ic.unbind(JNDIName);
      try {
        ic.close();        
      } catch (Exception e) {
        // ignore close error
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    return value;
  }

  /**
   * Takes apart an argument. Arguments are name<type>=value type field is
   * optional and defaults to String
   * 
   * @param ic
   * @param arg
   * @param value2
   * @param type2
   */
  private static void parseArg(InitialContext ic, String JNDIName, String type,
      String value) {

    Object valueObject = value;

    try {
      Class clazz = Class.forName(type);
      Constructor<String> constructor = clazz.getConstructor(String.class);
      valueObject = constructor.newInstance(value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    bindParam(ic, JNDIName, valueObject);
  }

  private static void bindParam(InitialContext ic, String name, Object value) {
    LOGGER.debug("Binding JNDI entry: " + name + " with value " + String.valueOf(value));
    try {
      Queue<String> n = split(name);
      Context nc = ic;
      while (n.size() > 1) {
        nc = checkAndCreateContext(n, nc);
      }
      bind(value, n.remove(), nc);
    } catch (NamingException e) {
	LOGGER.trace("NamingException while binding "+ value +" to "+name, e);
      throw new RuntimeException(e);
    }
  }

  private static void bind(Object value, String name, Context nc)
      throws NamingException {
    nc.bind(name, value);
  }
  public static void bindString(String key, String value)
      throws NamingException {
    InitialContext initialContext = new InitialContext();
    try {
      initialContext.bind(key, value);
      
    } catch (NameAlreadyBoundException e) {
      // let's unbind and bind again
      initialContext.rebind(key, value);
    } 
  }
  public static String lookupString(String key)
      throws NamingException {
    String myString = InitialContext.doLookup("java:"+key);
    return myString;
  }

  private static Context checkAndCreateContext(Queue<String> n, Context nc)
      throws NamingException {
    String s = n.remove();
    if (checkContext(nc, s)) {
      nc = (Context) nc.lookup(s);
    } else {
      nc = nc.createSubcontext(s);
    }
    return nc;
  }

  private static Queue<String> split(String name) {
    Queue<String> retVal = new LinkedList<String>();
    for (String s : name.split("/")) {
      for (String ss : s.split("\\.")) {
        retVal.add(ss);
      }
    }
    return retVal;
  }

  private static boolean checkContext(Context nc, String name) {
    boolean retVal = true;
    try {
      retVal = nc.lookup(name) != null;
    } catch (NamingException e) {
      retVal = false;
    }
    return retVal;
  }
}