package eu.europa.ec.mare.usm.administration.service.policy.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.ChangePassword;
import eu.europa.ec.mare.usm.administration.domain.PolicyDefinition;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.service.PasswordDigester;
import eu.europa.ec.mare.usm.administration.service.policy.DefinitionService;
import eu.europa.ec.mare.usm.administration.service.policy.PasswordPolicyEnforcer;
import eu.europa.ec.mare.usm.administration.service.user.impl.ManageUserValidator;
import eu.europa.ec.mare.usm.administration.service.user.impl.UserJpaDao;
import eu.europa.ec.mare.usm.information.entity.PasswordHistEntity;
import eu.europa.ec.mare.usm.information.entity.UserEntity;

/**
 * J2EE Stateless implementation of the PasswordPolicyEnforcer.<br/>
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PasswordPolicyEnforcerBean implements PasswordPolicyEnforcer{
  private static final Logger LOGGER = LoggerFactory.getLogger(PasswordPolicyEnforcerBean.class);
  private static final String PASSWORD_SUBJECT = "Password";
  private static final String MIN_HISTORY   = "password.minHistory";
  private static final String MIN_LENGTH    = "password.minLength";
  private static final String MIN_DIGITS    = "password.minDigits";
  private static final String MIN_SPECIAL   = "password.minSpecial";
  private static final String MIX_UPPER_LOWER_CASE = "password.mixUpperLowerCase";
  private static final String MAX_VALIDITY  = "password.maxValidity";
  private static final String MUST_CONTAIN  = "Password must contain at least ";
  private static final String USED_RECENTLY = "Password used too recently";

  @EJB
  DefinitionService definitionService;

  @Inject
  private UserJpaDao userDao;

  @Inject
  private PasswordDigester digester;
  
  @Inject
  private ManageUserValidator validator;
  
  @Override
  public Date assertValid(ServiceRequest<ChangePassword> request) 
  throws IllegalArgumentException, RuntimeException 
  {
    LOGGER.info("assertValid(" + request +") - (ENTER)");
    
    validator.assertValidChangePassword(request, null);
    
    Date ret = null;
    
    PolicyDefinition def = definitionService.getDefinition(PASSWORD_SUBJECT);

    if (def != null) {
      Properties properties = def.getProperties();
      String password = request.getBody().getNewPassword();
      checkLength(properties, password);
      checkDigits(properties, password);
      checkSpecial(properties, password);
	  checkMixUpperLowerCase(properties, password);
      checkHistory(properties, request.getBody());

      ret = checkValidity(properties);
    }
    
    LOGGER.info("assertValid() - (LEAVE): " + ret);
    return ret;
  }

  private void checkHistory(Properties properties, ChangePassword request) 
  {
    int minHistory = getInt(properties, MIN_HISTORY, 0);
    
    LOGGER.debug(MIN_HISTORY + ": " + minHistory);

    if (minHistory != 0) {
      String hash = digester.hashPassword(request.getNewPassword());
      
      UserEntity usr = userDao.read(request.getUserName());
      if (usr != null && hash.equals(usr.getPassword())) {
        throw new IllegalArgumentException(USED_RECENTLY);
      }

      minHistory -= 1;
              
      List<PasswordHistEntity> lst = userDao.getPasswordHistory(request.getUserName());
      if (lst != null && !lst.isEmpty()) {
        for (int i = 0; i < minHistory && i < lst.size(); i++) {
          if (hash.equals(lst.get(i).getPassword())) {
            throw new IllegalArgumentException(USED_RECENTLY);
          }
        }
      }
    }
  }
  
  private Date checkValidity(Properties properties)
  {
    int maxValidity = getInt(properties, MAX_VALIDITY, 0);
    
    LOGGER.debug(MAX_VALIDITY + ": " + maxValidity);

    Date ret = null;
    if (maxValidity != 0) {
      Calendar c = Calendar.getInstance();
      
      c.add(Calendar.DAY_OF_YEAR, maxValidity);
      
      ret = c.getTime();
    }
    
    return ret;
  }
  
  private void checkLength(Properties properties, String password)
  {
    int minLenth = getInt(properties, MIN_LENGTH, 0);
    
    LOGGER.debug(MIN_LENGTH + ": " + minLenth);
    
    if (minLenth != 0) {
      if (password.length() < minLenth) {
        throw new IllegalArgumentException(MUST_CONTAIN + minLenth + 
                                           " characters");
      }
    }
  }
  
  private void checkDigits(Properties properties, String password)
  {
    int minDigits = getInt(properties, MIN_DIGITS, 0);
    
    LOGGER.debug(MIN_DIGITS + ": " + minDigits);
    
    if (minDigits != 0) {
      int digits = 0;
      
      for (int i = 0; i < password.length(); i++) {
        char c = password.charAt(i);
        if (Character.isDigit(c)) {
          digits += 1;
        }
      }
      if (digits < minDigits) {
        throw new IllegalArgumentException(MUST_CONTAIN + minDigits + 
                                           " digits");
      }
    }
  }
  
  private void checkSpecial(Properties properties, String password)
  {
    int minSpecial = getInt(properties, MIN_SPECIAL, 0);
    
    LOGGER.debug(MIN_SPECIAL + ": " +minSpecial);
    
    if (minSpecial != 0) {
      int special = 0;
      
      for (int i = 0; i < password.length(); i++) {
        char c = password.charAt(i);
        
        if (!Character.isLetterOrDigit(c)) {
          special += 1;
        }
      }
      if (special < minSpecial) {
        throw new IllegalArgumentException(MUST_CONTAIN + minSpecial + 
                                           " special characters");
      }
    }
  }
  
  private void checkMixUpperLowerCase(Properties properties, String password)
  {
	boolean mixUpperLowercase = getBoolean(properties, MIX_UPPER_LOWER_CASE, false);
	
    LOGGER.debug(MIX_UPPER_LOWER_CASE + ": " +mixUpperLowercase);
    
    if (mixUpperLowercase != false) {
      int uppercase = 0;
      int lowercase = 0;
      
      for (int i = 0; i < password.length(); i++) {
        char c = password.charAt(i);
        
        if (Character.isLetter(c)) {
			if (Character.isUpperCase(c)) {
				uppercase += 1;	
			} else {
				lowercase += 1;
			}
        }
      }
      if (uppercase < 1 || lowercase < 1) {
        throw new IllegalArgumentException(MUST_CONTAIN + 
                                           "one uppercase and one lowercase characters");
      }
	}    
  }
  
  private int getInt(Properties properties, String key, int defaultValue)
  {
    int ret = defaultValue;
    
    if (properties != null) {
      String t = properties.getProperty(key);
      if (t != null) {
        try {
          ret = Integer.parseInt(t);
        } catch (Exception e) {
          LOGGER.warn("Property value for '" + key + 
                      "' is not an integer number. Using default value: " + 
                      defaultValue);
        }
      }
    }
    
    return ret;
  }
  
  private boolean getBoolean(Properties properties, String key, boolean defaultValue)
  {
    boolean ret = defaultValue;
    
    if (properties != null) {
      String t = properties.getProperty(key);
      if (t != null) {
        try {
          ret = Boolean.parseBoolean(t);
        } catch (Exception e) {
          LOGGER.warn("Property value for '" + key + 
                      "' is not boolean. Using default value: " + 
                      defaultValue);
        }
      }
    }
    
    return ret;
  }

  
  @Override
	public String getPasswordPolicy() {
		StringBuilder result = new StringBuilder();

		PolicyDefinition def = definitionService.getDefinition(PASSWORD_SUBJECT);

		result.append(MUST_CONTAIN).append(getInt(def.getProperties(), MIN_LENGTH, 0)).append(" characters")
				.append(", ").append(getInt(def.getProperties(), MIN_DIGITS, 0)).append(" digits").append(", ")
				.append(getInt(def.getProperties(), MIN_SPECIAL, 0)).append(" special characters");
		
		if(getBoolean(def.getProperties(), MIX_UPPER_LOWER_CASE, false)) {
			result.append(", ").append("1 upper and 1 lower case characters");
		}

		return result.toString();

	}
	    
	  
  
  
}
