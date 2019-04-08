package eu.europa.ec.mare.usm.jwt;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.Queue;
import javax.naming.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JndiUtil {

    private static InitialContext ic = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(JndiUtil.class);

    private JndiUtil() {}
    
    public static void createJNDI(String jndiName, String value) {

        try {
            ic = new InitialContext();

            bindParam(ic, jndiName, value);
            try {
                ic.close();
            } catch (Exception e) {
                // ignore close error
            }

        } catch (Exception e) {
            LOGGER.warn("Could not bind jndi: {}", jndiName);
        }

    }

    public static Object lookup(String jndiName) {
        Object value = null;
        try {
            ic = new InitialContext();

            value = ic.lookup(jndiName);
            try {
                ic.close();
            } catch (Exception e) {
                // ignore close error
            }

        } catch (Exception e) {
            LOGGER.warn("Could not lookup jndi: {}", jndiName);
        }
        return value;
    }

    public static Object unbind(String jndiName) {
        Object value = null;
        try {
            ic = new InitialContext();

            value = ic.lookup(jndiName);
            ic.unbind(jndiName);
            try {
                ic.close();
            } catch (Exception e) {
                // ignore close error
            }

        } catch (Exception e) {
            LOGGER.warn("Could not unbind jndi: {}", jndiName);
        }
        return value;
    }

    /**
     * Takes apart an argument. Arguments are name<type>=value type field is optional and defaults to String
     * 
     * @param ic
     * @param arg
     * @param value2
     * @param type2
     */
    private static void parseArg(InitialContext ic, String jndiName, String type, String value) {

        Object valueObject = value;

        try {
            Class clazz = Class.forName(type);
            Constructor<String> constructor = clazz.getConstructor(String.class);
            valueObject = constructor.newInstance(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        bindParam(ic, jndiName, valueObject);
    }

    private static void bindParam(InitialContext ic, String name, Object value) {
        LOGGER.debug("Binding JNDI entry: {} with value {}", name, String.valueOf(value));
        try {
            Queue<String> n = split(name);
            Context nc = ic;
            while (n.size() > 1) {
                nc = checkAndCreateContext(n, nc);
            }
            bind(value, n.remove(), nc);
        } catch (NamingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void bind(Object value, String name, Context nc) throws NamingException {
        nc.bind(name, value);
    }

    public static void bindString(String key, String value) throws NamingException {
        InitialContext initialContext = new InitialContext();
        try {
            initialContext.bind(key, value);

        } catch (NameAlreadyBoundException e) {
            // let's unbind and bind again
            initialContext.rebind(key, value);
        }
    }

    public static String lookupString(String key) throws NamingException {
        return InitialContext.doLookup("java:" + key);
    }

    private static Context checkAndCreateContext(Queue<String> n, Context nc) throws NamingException {
        String s = n.remove();
        if (checkContext(nc, s)) {
            nc = (Context) nc.lookup(s);
        } else {
            nc = nc.createSubcontext(s);
        }
        return nc;
    }

    private static Queue<String> split(String name) {
        Queue<String> retVal = new LinkedList<>();
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
