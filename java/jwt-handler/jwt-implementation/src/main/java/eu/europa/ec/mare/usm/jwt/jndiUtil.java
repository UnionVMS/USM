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
      e.printStackTrace();
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
