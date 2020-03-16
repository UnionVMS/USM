package eu.europa.ec.mare.usm.information.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.util.Arrays;
import java.util.List;

/**
 * Provides operations for the validation and authorisation of service requests
 */
@Stateless
public class RequestValidator {
    protected static final Logger LOGGER = LoggerFactory.getLogger(RequestValidator.class);

    public void assertNotTooLong(String name, int maxLen, String value) {
        if (value != null && value.length() > maxLen) {
            throw new IllegalArgumentException(name + " is too long (max " + maxLen + ")");
        }
    }

    public void assertNotTooShort(String name, int minLen, String value) {
        if (value != null && value.length() < minLen) {
            throw new IllegalArgumentException(name + " is too short (min " + minLen + ")");
        }
    }

    public void assertNotEmpty(String name, String value) {
        assertNotNull(name, value);
        if (value.trim().length() == 0) {
            throw new IllegalArgumentException(name + " must be defined");
        }
    }

    public void assertNotNull(String name, Object value) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must be defined");
        }
    }

    protected void assertInList(String name, String[] listOfValues, String value) {
      List<String> asList = Arrays.asList(listOfValues);
      assertInList(name, asList, value);
    }

    protected void assertInList(String name, List<String> listOfValues, String value) {
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
