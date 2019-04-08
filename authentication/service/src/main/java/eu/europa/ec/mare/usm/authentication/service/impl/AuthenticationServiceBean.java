package eu.europa.ec.mare.usm.authentication.service.impl;

import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;
import eu.europa.ec.mare.usm.service.impl.RequestValidator;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationQuery;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import eu.europa.ec.mare.usm.authentication.service.AuthenticationService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

/**
 * Stateless Session Bean implementation of the AuthenticationService.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AuthenticationServiceBean implements AuthenticationService {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceBean.class);
  private static final String AUTHENTICATION_SUBJECT = "Authentication";
  private static final String RENEWAL_REMINDER = "password.renewalReminder";
  private static final String LOCKOUT_DURATION = "account.lockoutDuration";
  private static final String LOCKOUT_FRESHOLD = "account.lockoutFreshold";
  private static final String LDAP_ENABLED = "ldap.enabled";
  private static final long ONE_MINUTE = (1000L * 60L);
  private static final long ONE_DAY = (1000L * 60 * 60 * 24);
  private static final String LOCKED = "L";
  private static final String DISABLED = "D";
  private static final String ENABLED = "E";

  @EJB
  private AuthenticationDao dao;

  @EJB
  private PolicyProvider policyProvider;

  @Inject
  private RequestValidator validator;
  
  
  @Override
  public boolean isLDAPEnabled() 
  {
    LOGGER.info("isLDAPEnabled() - (ENTER)");
  
    Properties props = policyProvider.getProperties(AUTHENTICATION_SUBJECT);
    
    boolean ret = Boolean.parseBoolean(props.getProperty(LDAP_ENABLED, "false"));

    LOGGER.info("isLDAPEnabled() - (LEAVE): " + ret);
    return ret;
  }

  @Override
  public boolean isPasswordExpired(String userName)
  {
	LOGGER.info("isPasswordExpired() - (ENTER)");
    
	boolean ret = false;
	
    dao.recordLoginSuccess(userName);
    
	Date now = new Date();
	Date expiry = dao.getPasswordExpiry(userName);
	if (expiry != null) {
		String newstring = new SimpleDateFormat("yyyy-MM-dd").format(expiry);
		if (expiry.before(now)) {
			//ret.setStatusCode(AuthenticationResponse.PASSWORD_EXPIRED);
			ret = true;
		} 		
	}
    
    LOGGER.info("isPasswordExpired() - (LEAVE)");
    return ret;	  
  }
  
  @Override
  public boolean isPasswordAboutToExpire(String userName)
  {
	LOGGER.info("isPasswordAboutToExpire() - (ENTER)");
    
	boolean ret = false;
	
    dao.recordLoginSuccess(userName);
    
	Date now = new Date();
	Date expiry = dao.getPasswordExpiry(userName);
	if (expiry != null) {
		String newstring = new SimpleDateFormat("yyyy-MM-dd").format(expiry);
	    Properties props = policyProvider.getProperties(AUTHENTICATION_SUBJECT);
	    int days = policyProvider.getIntProperty(props, RENEWAL_REMINDER, 0);							  
	    if (days != 0) {
          Date reminder = new Date(expiry.getTime() - (days * ONE_DAY));		
		  if (reminder.before(now)) {
		    //ret.setStatusCode(AuthenticationResponse.MUST_CHANGE_PASSWORD);
		    ret = true;
		  }
	    }			
	}
    
    LOGGER.info("isPasswordAboutToExpire() - (LEAVE)");
    return ret;	  
  }
  
  @Override
  public AuthenticationResponse authenticateUser(AuthenticationRequest request) 
  {
    LOGGER.info("authenticateUser(" + request + ") - (ENTER)");
	//System.out.format("authenticate: %s - %s\n", request.getUserName(), request.getPassword());		
    validator.assertValid(request);

    AuthenticationResponse ret;
    if (isLDAPEnabled()) {
	  //System.out.format("authenticate via LDAP\n");		
      ret = authenticateLdap(request);
    } else {
	  //System.out.format("authenticate via local\n");		
      ret = authenticateLocal(request);
    }
    if (ret.isAuthenticated()) {
	  //System.out.format("isAuthenticated\n");		
      Date now = new Date();
      Date expiry = handleLoginSuccess(ret.getUserMap(), request.getUserName());
      if (expiry != null) {
		String newstring = new SimpleDateFormat("yyyy-MM-dd").format(expiry);	  
        if (expiry.before(now)) {
          ret.setStatusCode(AuthenticationResponse.PASSWORD_EXPIRED);
        } else {
          Properties props = policyProvider.getProperties(AUTHENTICATION_SUBJECT);
          int days = policyProvider.getIntProperty(props, RENEWAL_REMINDER, 0);
          if (days != 0) {
            Date reminder = new Date(expiry.getTime() - (days * ONE_DAY));		
            if (reminder.before(now)) {
			  //System.out.format("MUST CHANGE PASSWORD\n");						
              ret.setStatusCode(AuthenticationResponse.MUST_CHANGE_PASSWORD);
            }
          }
        }
      }
    } else {
      handleLoginFailure(request.getUserName());
    }
    LOGGER.info("authenticateUser() - (LEAVE): " + ret);
    return ret;
  }

  @Override
  public ChallengeResponse getUserChallenge(AuthenticationQuery query) 
  {
    LOGGER.info("getUserChallenge(" + query + ") - (ENTER)");

    validator.assertValid(query);

    ChallengeResponse ret = null;
    try {
      List<ChallengeResponse> lst = dao.getUserChallenges(query.getUserName());

      if (lst != null && !lst.isEmpty()) {
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(lst.size());
        ret = lst.get(index);
      }
    } catch (Exception exc) {
      throw new RuntimeException("Problem: " + exc.getMessage(), exc);
    }

    LOGGER.info("getUserChallenge() - (LEAVE): " + ret);
    return ret;
  }

  @Override
  public AuthenticationResponse authenticateUser(ChallengeResponse request) 
  {
    LOGGER.info("authenticateUser(" + request + ") - (ENTER)");
    validator.assertValid(request);

    AuthenticationResponse ret = new AuthenticationResponse();
    try {
      Long uid = dao.getActiveUserId(request);

      ret.setAuthenticated(uid != null);
      if (ret.isAuthenticated()) {
        ret.setStatusCode(AuthenticationResponse.SUCCESS);
      } else {
        ret.setStatusCode(AuthenticationResponse.INVALID_CREDENTIALS);
      }
    } catch (Exception exc) {
      LOGGER.error("Problem: " + exc.getMessage(), exc);
      ret.setStatusCode(AuthenticationResponse.INTERNAL_ERROR);
    }

    LOGGER.info("authenticateUser(" + request.getUserName() + ") - (LEAVE): " + ret);
    return ret;
  }

  private AuthenticationResponse authenticateLocal(AuthenticationRequest request) 
  {
    LOGGER.debug("authenticateLocal(" + request + ") - (ENTER)");
    AuthenticationResponse ret = createResponse();
    
    try {
      String password = hashPassword(request.getPassword());
      Long uid = dao.getActiveUserId(request.getUserName(), password);
	  String lockoutReason = dao.getLockoutReason(request.getUserName());
      if (uid != null) {
        ret.setAuthenticated(true);
        ret.setStatusCode(AuthenticationResponse.SUCCESS);
      } else {
        String status = dao.getUserStatus(request.getUserName());
        if (ENABLED.equals(status)) {
          // Invalid password
          ret.setStatusCode(AuthenticationResponse.INVALID_CREDENTIALS);
        } else if (status == null) {
          // Invalid userName
          ret.setStatusCode(AuthenticationResponse.INVALID_CREDENTIALS);
        } else if (DISABLED.equals(status)) {
          ret.setStatusCode(AuthenticationResponse.ACCOUNT_DISABLED);
        } else if (LOCKED.equals(status)) {
          ret.setStatusCode(AuthenticationResponse.ACCOUNT_LOCKED);
		  ret.setErrorDescription(lockoutReason);
        } else {
          ret.setStatusCode(AuthenticationResponse.OTHER);
        }
      }
    } catch (Exception exc) {
      LOGGER.error("Problem: " + exc.getMessage(), exc);
      ret.setStatusCode(AuthenticationResponse.INTERNAL_ERROR);
    }
    
    LOGGER.debug("authenticateLocal() - (LEAVE): " + ret);
    return ret;
  }

  private AuthenticationResponse authenticateLdap(AuthenticationRequest request) 
  {
    LOGGER.debug("authenticateLdap(" + request + ") - (ENTER)");
    AuthenticationResponse ret = createResponse();

    LDAP ldap = new LDAP(policyProvider.getProperties(AUTHENTICATION_SUBJECT));
    Map<String, Object> userMap = ldap.authenticate(request.getUserName(),
            request.getPassword());
    LOGGER.debug("ldap.authenticate: " + userMap);
    
    if (userMap != null) {
      if (userMap.get(LDAP.STATUS_CODE) == null) {
        String status = dao.getUserStatus(request.getUserName());
        if (ENABLED.equals(status)) {
          ret.setAuthenticated(true);
          ret.setStatusCode(AuthenticationResponse.SUCCESS);
        } else if (DISABLED.equals(status)) {
          ret.setStatusCode(AuthenticationResponse.ACCOUNT_DISABLED);
        } else if (LOCKED.equals(status)) {
          ret.setStatusCode(AuthenticationResponse.ACCOUNT_LOCKED);
        } else {
          ret.setStatusCode(AuthenticationResponse.OTHER);
        }
      } else {
        ret.setStatusCode(((Integer) userMap.get(LDAP.STATUS_CODE)));
      }
    }
    
    if (ret.isAuthenticated())	{
    	ret.setUserMap(userMap);
    }

    LOGGER.debug("authenticateLdap() - (LEAVE): " + ret);
    return ret;
  }

  private AuthenticationResponse createResponse() 
  {
    AuthenticationResponse ret = new AuthenticationResponse();
    
    ret.setAuthenticated(false);
    ret.setStatusCode(AuthenticationResponse.INVALID_CREDENTIALS);
    
    return ret;
  }

  private String hashPassword(String password)
  throws NoSuchAlgorithmException 
  {
    String ret = null;

    if (password != null) {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(password.getBytes());

      byte[] digest = md.digest();

      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < digest.length; i++) {
        sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
      }
      ret = sb.toString();
    }

    return ret;
  }

  private Date handleLoginSuccess(Map<String, Object> userMap, String userName)
  {
    LOGGER.debug("handleLoginSuccess(" + userName + ") - (ENTER)");
    
    dao.recordLoginSuccess(userName);
    
    Date ret = dao.getPasswordExpiry(userName);
    
    if (isLDAPEnabled() && userMap != null && !userMap.isEmpty()) {
    	handleSyncWithLDAP(userMap, userName);
    } else {
    	LOGGER.debug("No handle sync with LDAP need");
    }
    
    LOGGER.info("handleLoginSuccess() - (LEAVE)");
    return ret;
  }
  
  private void handleSyncWithLDAP(Map<String,Object> userMap, String userName) {
	  int personId = dao.getPersonId(userName);
	  if (personId == 0) {
		  dao.createPersonForUser(userMap, userName);
	  } else {
		  dao.syncPerson(userMap, personId);
	  }
  }
  
  private boolean handleLoginFailure(String userName) 
  {
    LOGGER.debug("handleLoginFailure(" + userName + ") - (ENTER)");
    
    dao.recordLoginFailure(userName);
    
    boolean ret = false;
    
    Properties props = policyProvider.getProperties(AUTHENTICATION_SUBJECT);
    int freshold = policyProvider.getIntProperty(props, LOCKOUT_FRESHOLD, 0);
    int duration =  policyProvider.getIntProperty(props, LOCKOUT_DURATION, 0);
    if (freshold != 0 && duration != 0) {
      int failures = dao.getLoginFailures(userName);
      if (failures >= freshold) {
        Date lockoutExpiry = new Date (System.currentTimeMillis() + 
                                       duration * ONE_MINUTE);
        dao.lockUser(userName, lockoutExpiry);
      }
    }
    
    LOGGER.info("handleLoginFailure() - (LEAVE): " + ret);
    return ret;
  }
}
