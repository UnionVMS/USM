package eu.europa.ec.mare.usm.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
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
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the creation, extension (of validity) and verification (parsing) 
 * of JWT tokens.
 */

@Singleton
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
  private static final String DEFAULT_KEY = "usmSecretKey";
  private static final String DEFAULT_ID = "usm/authentication";
  private static final String DEFAULT_ISSUER = "usm";
  private static final String DEFAULT_SUBJECT = "authentication";
  private static byte[] secretKey;

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
        LOGGER.warn("Failed to load class-path resource:'" + PROPERTIES_FILE
            + "'. Using default values", e);
      }
    } else {
      LOGGER.debug("Class-path resource: '" + PROPERTIES_FILE
          + "' does not exist. Using default values");
    }
    initKey();
  }
  
  @PreDestroy
  public void destroy(){
    //removeKey();
  }

  /**
   * Creates a JWT token identifying the user with the provided name.
   * 
   * @param userName the user name.
   * 
   * @return a JWT token identifying the user, or null if the provided was 
   * null or empty
   */
  public String createToken(String userName) {
    LOGGER.debug("createToken(" +  userName + ") - (ENTER)");
    
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
   * @param token the JWT token to be extended
   * 
   * @return an extended (validity) version of the token, or null if the 
   * provided input was invalid or already expired.
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
   * @param token the JWT token to be parsed
   * 
   * @return the name of the user, or null if the provided input was invalid 
   * or expired.
   */
  public String parseToken(String token) 
  {
    LOGGER.debug("parseToken(" + token + ") - (ENTER)");
    
    String ret = null;

    Claims claims = parseClaims(token);
    if (claims != null) {
      ret = (String) claims.get(USER_NAME);
    }

    LOGGER.debug("parseToken() - (LEAVE)");
    return ret;
  }

  static private String signClaims(Claims claims) 
  {
    // Header
    Map<String, Object> header = new HashMap<>();
    header.put(Header.TYPE, Header.JWT_TYPE);
    header.put(JwsHeader.ALGORITHM, signatureAlgorithm);
    
    // Signature key
    Key key = new SecretKeySpec(getSecretKey(), 
                                signatureAlgorithm.getJcaName());

    String ret = Jwts.builder().
            setHeader(header).
            setClaims(claims).
            signWith(signatureAlgorithm, key).
            compact();
  
    return ret;
  }

  static private Claims parseClaims(String token) {
    Claims ret = null;

    if (token != null && !token.trim().isEmpty()) {
      try {
        ret = Jwts.parser().
                   setSigningKey(getSecretKey()).
                   parseClaimsJws(token).getBody();
      } catch (ExpiredJwtException e) {
        LOGGER.error("Token expired", e);
      } catch (UnsupportedJwtException |
              MalformedJwtException | SignatureException |
              IllegalArgumentException e) {
        LOGGER.error("Failed to parse token", e);
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
      retString.append(
          Integer.toHexString(0x0100 + (publicKey[i] & 0x00FF)).substring(1));
    }
    return retString.toString();

  }

  static private void initKey() {
    // get the key from properties if set
    
    String key = System.getProperty(SYSTEM_KEY);
    String JNDIkey = "USM/"+PROP_KEY;
    String source = "System property "+SYSTEM_KEY;
    if (key == null || key.isEmpty()) {
      LOGGER.debug("no key defined in system property " + SYSTEM_KEY
          + ". Checking in properties file");
      key = properties.getProperty(PROP_KEY);
      source = "Read property "+PROP_KEY;
      if (key == null || key.isEmpty()) {
        LOGGER.debug("no key defined in property. Checking in JNDI context");
        // let's look if a key exist in JNDI
        try {
          key = jndiUtil.lookup(JNDIkey).toString();
          source = "JNDI key binding "+JNDIkey;
          LOGGER.debug("Found Key bound to JNDI name " + JNDIkey + " with value "+ key);
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
    LOGGER.debug("Secret Key set to: " + key);
    LOGGER.info("Secret key source: " + source);
    if(source!= ("JNDI key binding "+JNDIkey) ){
      try {
        jndiUtil.createJNDI(JNDIkey, key);
        LOGGER.debug("Key now bound to JNDI name " + JNDIkey);
      } catch (Exception e) {
        LOGGER.error("Error binding key",e);
      }
    }
    secretKey = DatatypeConverter.parseBase64Binary(key);
    LOGGER.debug("parsed Base64 key: " + secretKey);
  }
  
  
  static private void removeKey() {
    // get the key from properties if set
    String key = properties.getProperty(PROP_KEY);
    String JNDIkey = "USM/"+PROP_KEY;
      // let's look if a key exist in JNDI
      try {      
        key = (String) jndiUtil.lookup(JNDIkey);
        LOGGER.debug("Key bound to JNDI name " + PROP_KEY);
        try {      
          jndiUtil.unbind(JNDIkey);
          LOGGER.debug("Unbound JNDI name " + PROP_KEY);
        } catch (Exception e) {
          LOGGER.error("Error unbinding key",e);
        }
       
      } catch (Exception e) {
        LOGGER.error("Error looking up key",e);
      }
      
    secretKey = null;
  }

  static private byte[] getSecretKey() {
    return secretKey;
  }
}
