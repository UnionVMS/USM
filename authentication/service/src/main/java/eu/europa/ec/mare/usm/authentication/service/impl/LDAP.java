package eu.europa.ec.mare.usm.authentication.service.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;

public class LDAP {
	private static final Logger LOGGER = LoggerFactory.getLogger(LDAP.class);
	private static final String LOCKED = "Bind failed: account was permanently locked]";
	private static final String INVALID_CREDENTIALS = "[LDAP: error code 49"; 
	public static final String STATUS_CODE = "statusCode";
	
	public static final String LDAP_QUERY_ATTRIBUTES = "ldap.query.attributes";
	
	private String serverURL;
	private String contextRoot;	
	private String bindDN;
	private String bindPassword;
	private String queryFilter;
	private String queryAttributes;

  /**
   * Creates a new instance and configures it using the provided properties.
   * 
   * @param properties the LDAP configuration properties
   */
	public LDAP(Properties properties) 
  {
		init(properties);
	}

  /**
   * Authenticated a user with the provided credentials.
   * 
   * @param userName the user name
   * @param password the user password
   * 
   * @return a possibly-empty Map containing user attributes retrieved from the 
   * LDAP server in case of successful authentication, or an authentication 
   * response 'statusCode' in case of failed authentication.
   */
	public Map<String, Object> authenticate(String userName, String password) 
  {
    LOGGER.info("authenticate(" + userName + ") - (ENTER)");
    
		SearchControls searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    if (queryAttributes != null && !queryAttributes.trim().isEmpty()) {
      String returnedAtts[] = queryAttributes.split(",");
      searchCtls.setReturningAttributes(returnedAtts);
		}
    
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, serverURL);
		if (bindDN != null && !bindDN.trim().isEmpty() && bindPassword != null) {
      env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, bindDN.trim());
			env.put(Context.SECURITY_CREDENTIALS, bindPassword.trim());
			LOGGER.debug("LDAP search by techdn: " + bindDN);
		} else {
			LOGGER.debug("LDAP anonymous search");
		}

    Map<String, Object> ret = null;
    
		LdapContext ctx = null;
		NamingEnumeration<SearchResult> answer = null;
		NamingEnumeration<? extends Attribute> ne = null;
		try {
			ctx = new InitialLdapContext(env, null);
			String searchFor = MessageFormat.format(queryFilter, userName);
			LOGGER.info("LDAP search for " + searchFor + " under root " + contextRoot);
			answer = ctx.search(contextRoot, searchFor, searchCtls);
			LOGGER.info("LDAP search for " + searchFor + " got answer");
			while (answer.hasMoreElements()) {
				SearchResult sr = answer.next();
				String dn = sr.getNameInNamespace();
				if (dn == null || answer.hasMoreElements()) {
					answer.close();
					ctx.close();
					return null;
				}
				Attributes attrs = sr.getAttributes();
				if (attrs != null) {
					ret = new HashMap<String, Object>();
					ne = attrs.getAll();
					while (ne.hasMore()) {
						Attribute attr = (Attribute) ne.next();
						ret.put(attr.getID(), attr.get());
					}
					ret.put(LDAP_QUERY_ATTRIBUTES, queryAttributes);
					ne.close();
				}
				answer.close();
				LOGGER.info("LDAP search for " + searchFor + " answer closed");
				ctx.close();
				LOGGER.info("LDAP search for " + searchFor + " initial context closed");
				
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
				env.put(Context.SECURITY_PRINCIPAL, dn);
				env.put(Context.SECURITY_CREDENTIALS, password);
				LOGGER.info("LDAP bind for " + dn);
				ctx = new InitialLdapContext(env, null);
				ctx.close();
			}
			LOGGER.info("LDAP operations ended.");
		} catch (NamingException e) {
			LOGGER.info("LDAP operations failed: " + e.getMessage()); 
			ret = new HashMap<String, Object>();
			String expl = null;
			if (e.getCause() instanceof AuthenticationException) {
				expl = ((AuthenticationException) e.getCause()).getExplanation();
			} else if (e instanceof AuthenticationException) {
				expl = ((AuthenticationException) e).getExplanation();
			}
      LOGGER.info("LDAP authentication exception explanation: " + expl);
			if (expl != null) {
				if (expl.startsWith(INVALID_CREDENTIALS)) {
					ret.put(STATUS_CODE, AuthenticationResponse.INVALID_CREDENTIALS);
				} else if (expl.endsWith(LOCKED)) {
					ret.put(STATUS_CODE, AuthenticationResponse.ACCOUNT_LOCKED);
				}  
			} else {
        LOGGER.error("LDAP internal error. " + e.getMessage(), e);
        ret.put(STATUS_CODE, AuthenticationResponse.INTERNAL_ERROR);
      }
		} finally {
			cleanUp(ctx, answer, ne);
		}
		
    LOGGER.info("authenticate() - (LEAVE): " + ret);
		return ret;
	}
	
	private void cleanUp(LdapContext ctx, 
                       NamingEnumeration<SearchResult> answer, 
                       NamingEnumeration<? extends Attribute> ne) 
  {
		if (ne != null) {
			try {
				ne.close();
			} catch (NamingException e) {
				LOGGER.warn("Fail to close attribute enumeration: " + e.getMessage(), e);
			}
		}
		if (answer != null) {
			try {
				answer.close();
			} catch (NamingException e) {
				LOGGER.warn("Fail to close answer: " + e.getMessage(), e);
			}
		}
		if (ctx != null) {
			try {
				ctx.close();
			} catch (NamingException e) {
				LOGGER.warn("Failed to close context: " + e.getMessage(), e);
			}
		}
	}
	
	private void init(Properties props) 
	{
	    serverURL = props.getProperty("ldap.server.url");
	    contextRoot = props.getProperty("ldap.context.root");
	    bindDN = props.getProperty("ldap.bind.dn");
	    bindPassword = props.getProperty("ldap.bind.password");
	    queryFilter = props.getProperty("ldap.query.filter");
	    queryAttributes = props.getProperty(LDAP_QUERY_ATTRIBUTES);
	}
	
	public static String[] getQueryAttributes(Map<String, Object> map) {
		String[] attrArray = null;
		
		if (map != null) {
			String attrs = (String) map.get(LDAP_QUERY_ATTRIBUTES);
			if (attrs != null) {
				attrs = attrs.trim();
				if (!attrs.isEmpty()) {
					attrArray = attrs.split(",");
					LOGGER.debug("LDAP Query attributes: " + attrArray.toString()); 
				}
			}
		} 
		return attrArray;
	}
}
