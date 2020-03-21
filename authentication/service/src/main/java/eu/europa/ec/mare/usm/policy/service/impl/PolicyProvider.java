package eu.europa.ec.mare.usm.policy.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * J2EE Singleton that provides configuration/definition properties
 * for the  user authentication and number of session policies.
 */
@Singleton
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PolicyProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyProvider.class);
    private static final long FIVE_MINUTES = (5 * 60 * 1000);

    @Inject
    private PolicyDao dao;

    private final Map<String, TimedKeeper> cache;

    public PolicyProvider() {
        cache = new HashMap<>();
    }

    public void reset() {
        LOGGER.debug("reset() - (ENTER)");
        clear();
        LOGGER.debug("reset() - (LEAVE)");
    }

    /**
     * Retrieves configuration/definition properties for the provided
     * policy subject.
     *
     * @param subject the policy subject
     * @return the possibly-empty configuration/definition properties
     */
    public Properties getProperties(String subject) {
        LOGGER.debug("getProperties(" + subject + ") - (ENTER)");

        PolicyDefinition def = null;

        TimedKeeper tk = cache.get(subject);
        if (tk != null) {
            if (tk.timestamp + FIVE_MINUTES > System.currentTimeMillis()) {
                def = tk.definition;
            } else {
                // Evict expired cached version
                evictDefinition(subject);
            }
        }

        if (def == null) {
            def = readDefinition(subject);
            cacheDefinition(subject, def);
        }

        Properties ret = def.getProperties();

        LOGGER.debug("getProperties() - (LEAVE)");
        return ret;
    }

    /**
     * Sets the configuration/definition properties for the provided
     * policy subject.
     *
     * @param subject    the policy subject
     * @param properties the new configuration/definition properties
     * @return the previously defined configuration/definition properties
     */
    public Properties setProperties(String subject, Properties properties) {
        LOGGER.debug("setProperties(" + properties + ") - (ENTER)");

        PolicyDefinition def = new PolicyDefinition();
        def.setSubject(subject);
        def.setProperties(properties);
        cacheDefinition(subject, def);

        Properties ret = properties;

        LOGGER.debug("setProperties() - (LEAVE)");
        return ret;
    }

    /**
     * Gets an integer (policy) property value.
     *
     * @param policy       the policy properties
     * @param propertyName the property name
     * @param defaultValue the default value
     * @return the property value if it exists and is an integer number, the
     * default value otherwise
     */
    public int getIntProperty(Properties policy, String propertyName,
                              int defaultValue) {
        int ret = defaultValue;

        try {
            ret = Integer.parseInt(policy.getProperty(propertyName,
                    Integer.toString(defaultValue)));
        } catch (NumberFormatException e) {
            LOGGER.error("Policy property value for '" + propertyName +
                    "' is not parsable: " + e.getMessage());
        }

        return ret;
    }

    private PolicyDefinition readDefinition(String subject) {
        PolicyDefinition ret = new PolicyDefinition();

        ret.setSubject(subject);
        ret.setProperties(dao.getProperties(subject));

        return ret;
    }

    private void cacheDefinition(String subject, PolicyDefinition ret) {
        synchronized (cache) {
            cache.put(subject, new TimedKeeper(ret));
        }
    }

    private void evictDefinition(String subject) {
        synchronized (cache) {
            cache.remove(subject);
        }
    }

    private void clear() {
        synchronized (cache) {
            cache.clear();
        }
    }

    private class TimedKeeper {
        long timestamp;
        PolicyDefinition definition;

        private TimedKeeper(PolicyDefinition def) {
            definition = def;
            timestamp = System.currentTimeMillis();
        }
    }

}
