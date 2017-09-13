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

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Handles the creation, extension (of validity) and verification (parsing) of
 * JWT tokens.
 */

@Singleton
// todo: verify how we can avoid a global entry or alternatively set it up in a
// different place
// to allow for testing
// @EJB(name = "java:global/UsmJwtHandler", beanInterface =
// JwtTokenHandler.class)
@Startup
public class DefaultJwtTokenHandler implements JwtTokenHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenHandler.class);
  private static final String PROPERTIES_FILE = "/jwt.properties";
  private static final String SYSTEM_KEY = "USM.secretKey";
  private static final String PROP_KEY = "secretKey";
  private static final String PROP_SUBJECT = "subject";
  private static final String PROP_ISSUER = "issuer";
  private static final String PROP_ID = "id";
  private static final long DEFAULT_TTL = (30 * 60 * 1000);
  private static final String DEFAULT_KEY = "aSharedSecretKey";
  private static final String DEFAULT_ID = "usm/authentication";
  private static final String DEFAULT_ISSUER = "usm";
  private static final String DEFAULT_SUBJECT = "authentication";
  private static String secretKey;

  private static final String USER_NAME = "userName";

  private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
  static private Properties properties = new Properties();

  /**
   * Creates a new instance
   */
  public DefaultJwtTokenHandler() {

  }

  @PostConstruct
  public void init() {
    InputStream is = getClass().getResourceAsStream(PROPERTIES_FILE);
    if (is != null) {
      try {
        properties.load(is);
      } catch (IOException e) {
        LOGGER.warn("Failed to load class-path resource:'" + PROPERTIES_FILE + "'. Using default values", e);
      }
    } else {
      LOGGER.debug("Class-path resource: '" + PROPERTIES_FILE + "' does not exist. Using default values");
    }
    initKey();
  }

  @PreDestroy
  public void destroy() {
    // removeKey();
  }

  /**
   * Creates a JWT token identifying the user with the provided name.
   * 
   * @param userName
   *          the user name.
   * 
   * @return a JWT token identifying the user, or null if the provided was null
   *         or empty
   */
  public String createToken(String userName) {
    LOGGER.debug("createToken(" + userName + ") - (ENTER)");

    String ret = null;

    if (userName != null && !userName.trim().isEmpty()) {
      long now = System.currentTimeMillis();

      // Claims
      Claims claims = (Claims) Jwts.claims();
      claims.setId(properties.getProperty(PROP_ID, DEFAULT_ID));
      claims.setIssuer(properties.getProperty(PROP_ISSUER, DEFAULT_ISSUER));
      claims.setSubject(properties.getProperty(PROP_SUBJECT, DEFAULT_SUBJECT));
      claims.setIssuedAt(new Date(now));
      claims.setExpiration(new Date(now + DEFAULT_TTL));
      claims.put(USER_NAME, userName);

      ret = signClaims(claims);
    }

    LOGGER.debug("createToken() - (LEAVE)");
    return ret;
  }

  /**
   * Extends the validity period of the provided token.
   * 
   * @param token
   *          the JWT token to be extended
   * 
   * @return an extended (validity) version of the token, or null if the
   *         provided input was invalid or already expired.
   */
  public String extendToken(String token) {
    LOGGER.debug("extendToken(" + token + ") - (ENTER)");

    String ret = null;
    Claims claims = parseClaims(token);
    if (claims != null) {

      long now = System.currentTimeMillis();
      claims.setIssuedAt(new Date(now));
      claims.setExpiration(new Date(now + DEFAULT_TTL));

      ret = signClaims(claims);
    }

    LOGGER.debug("extendToken() - (LEAVE)");
    return ret;
  }

  /**
   * Extract the name of the user to which the provided token was issued.
   * 
   * @param token
   *          the JWT token to be parsed
   * 
   * @return the name of the user, or null if the provided input was invalid or
   *         expired.
   */
  public String parseToken(String token) {
    LOGGER.debug("parseToken(" + token + ") - (ENTER)");

    String ret = null;

    Claims claims = parseClaims(token);
    if (claims != null) {
      ret = (String) claims.get(USER_NAME);
    }

    LOGGER.debug("parseToken() - (LEAVE)");
    return ret;
  }

  static private String signClaims(Claims claims) {
    // Header
    Map<String, Object> header = new HashMap<>();
    header.put(Header.TYPE, Header.JWT_TYPE);
    header.put(JwsHeader.ALGORITHM, signatureAlgorithm);

    // Signature key
    Key key = new SecretKeySpec(getSecretKey(), signatureAlgorithm.getJcaName());

    String ret = Jwts.builder().setHeader(header).setClaims(claims).signWith(signatureAlgorithm, key).compact();

    return ret;
  }

  static private Claims parseClaims(String token) {
    Claims ret = null;

    if (token != null && !token.trim().isEmpty()) {
      try {
        ret = Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(token).getBody();
      } catch (ExpiredJwtException e) {
        LOGGER.warn("Token expired");
      } catch (SignatureException e) {
        LOGGER.warn("Token signature could not be verified");
      } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
        LOGGER.warn("Failed to parse token");
        LOGGER.trace("Exception while parsing token", e);
      }
    }

    return ret;
  }

  private static String genkey() throws NoSuchAlgorithmException {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(512);
    byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
    StringBuffer retString = new StringBuffer();
    for (int i = 0; i < publicKey.length; ++i) {
      retString.append(Integer.toHexString(0x0100 + (publicKey[i] & 0x00FF)).substring(1));
    }
    return retString.toString();

  }

  /**
   * Secret Key initialisation: This works by looking up possible key values in a specific order
   * 
   * If a java system property is set for USM.secretKey this will be used.
   * If not found we will look for a secretKey property in a jwt.properties file in the current war/EAR
   * If not found either we look for a JNDI entry under USM/secretKey.
   * Finally, if none of the above has yielded a value we generate a random key
   * 
   * If the source of the key is not a JNDI entry we then try to bind it and if we discover that
   * the key cannot be bound because there actually is a different key already bound we log an error
   * but use the key we found. Such a mismatch may result in a user being able to login but not 
   * being able to get data from other modules.
   * To avoid such issues use one of the following technique:
   * - Set a system property (this will override everything else)
   * - Remove any jwt.properties file present in the deployments and bind a JNDI entry using the 
   *   server configuration file or let the first module generate a random key for other modules 
   *   to pick up (but we could theoretically get into race conditions)
   * - Set the secretKey value in the jwt.properties file to the same value in every deployment 
   * 
   */
  static private void initKey() {
    // get the key from properties if set

    String key = System.getProperty(SYSTEM_KEY);
    String JNDIkey = "USM/" + PROP_KEY;
    String source = "System property " + SYSTEM_KEY;
    if (key == null || key.isEmpty()) {
      LOGGER.debug("no key defined in system property " + SYSTEM_KEY + ". Checking in properties file");
      key = properties.getProperty(PROP_KEY);
      source = "Read property " + PROP_KEY;
      if (key == null || key.isEmpty()) {
        LOGGER.debug("no key defined in property. Checking in JNDI context");
        // let's look if a key exist in JNDI
        try {
          key = jndiUtil.lookup(JNDIkey).toString();
          source = "JNDI key binding " + JNDIkey;
          LOGGER.debug("Found Key bound to JNDI name " + JNDIkey + " with value " + key);
        } catch (Exception e) {
          LOGGER.debug("No secret key bound to JNDI name " + JNDIkey);
        }
        if (key == null || key.isEmpty()) {
          source = "Generated";
          LOGGER.debug("no key in JNDI context. Generating a random one");
          // get a random key
          try {
            key = genkey();
          } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("Error Generating secret key. Using default value", e);
            key = DEFAULT_KEY;
            source = "DEFAULT_KEY";
          }
        }
      }
    }
    if (source != ("JNDI key binding " + JNDIkey)) {
      // see if there is a key bound already
      String previousBoundKey = "";
      try {
        previousBoundKey = jndiUtil.lookup(JNDIkey).toString();
        LOGGER.trace("Found Key bound to JNDI name " + JNDIkey + " with value " + previousBoundKey);
        if (previousBoundKey != key) {
          LOGGER.error("Key mismatch! There may be other modules that have already set a different secret key. Only"
              + " setting a system property " + SYSTEM_KEY + " will override all keys. A value set through a "
              + PROPERTIES_FILE + " file packaged in a deployment will be bound if there isn't already a "
              + "key bound to JNDI name " + JNDIkey + ". The current deployment will use the key from " + source
              + " but other module may be expecting the value found in JNDI. Set logging to trace for values");
        }
      } catch (Exception e) {
        LOGGER.trace("No secret key bound to JNDI name " + JNDIkey);
        // We only try to bind the key if the lookup failed
        try {
          jndiUtil.createJNDI(JNDIkey, key);
          LOGGER.debug("Key now bound to JNDI name " + JNDIkey);
        } catch (Exception er) {
          LOGGER.error("Error binding key", er);
        }
      }
    }

    LOGGER.trace("Secret Key set to: " + key);
    LOGGER.info("Secret key source: " + source);
    secretKey = key;
  }

  static private void removeKey() {
    // get the key from properties if set
    String key = properties.getProperty(PROP_KEY);
    String JNDIkey = "USM/" + PROP_KEY;
    // let's look if a key exist in JNDI
    try {
      key = (String) jndiUtil.lookup(JNDIkey);
      LOGGER.debug("Key bound to JNDI name " + PROP_KEY);
      try {
        jndiUtil.unbind(JNDIkey);
        LOGGER.debug("Unbound JNDI name " + PROP_KEY);
      } catch (Exception e) {
        LOGGER.error("Error unbinding key", e);
      }

    } catch (Exception e) {
      LOGGER.error("Error looking up key", e);
    }

    secretKey = null;
  }

  static private byte[] getSecretKey() {
    
    try {
      return secretKey.getBytes();
    } catch (Exception e) {
      return null;
    }
  }
}