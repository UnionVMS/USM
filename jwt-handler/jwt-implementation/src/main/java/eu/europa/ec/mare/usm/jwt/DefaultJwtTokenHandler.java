package eu.europa.ec.mare.usm.jwt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
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
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

/**
 * Handles the creation, extension (of validity) and verification (parsing) of JWT tokens.
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
    private static final String USER_NAME = "userName";
    private static final String FEATURES = "features";
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private byte[] secretKey;

    private Properties properties = new Properties();

    @PostConstruct
    public void init() {
        InputStream is = getClass().getResourceAsStream(PROPERTIES_FILE);
        if (is != null) {
            try {
                properties.load(is);
            } catch (IOException e) {
                LOGGER.warn("Failed to load class-path resource:'{}'. Using default values", PROPERTIES_FILE, e);
            }
        } else {
            LOGGER.debug("Class-path resource: '{}' does not exist. Using default values", PROPERTIES_FILE);
        }
        initKey();
    }

    /**
     * Creates a JWT token identifying the user with the provided name.
     * 
     * @param userName the user name.
     * 
     * @return a JWT token identifying the user, or null if the provided was null or empty
     */
    public String createToken(String userName) {
        return createToken(userName, null);
    }

    public String createToken(String userName, List<String> features) {
        LOGGER.debug("createToken( {} ) - (ENTER)", userName);

        String ret = null;

        if (userName != null && !userName.trim().isEmpty()) {
            long now = System.currentTimeMillis();

            // Claims
            Claims claims = Jwts.claims();
            claims.setId(properties.getProperty(PROP_ID, DEFAULT_ID));
            claims.setIssuer(properties.getProperty(PROP_ISSUER, DEFAULT_ISSUER));
            claims.setSubject(properties.getProperty(PROP_SUBJECT, DEFAULT_SUBJECT));
            claims.setIssuedAt(new Date(now));
            claims.setExpiration(new Date(now + DEFAULT_TTL));
            claims.put(USER_NAME, userName);
            if (features != null) {
                claims.put(FEATURES, features);
            }

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
     * @return an extended (validity) version of the token, or null if the provided input was invalid or already
     *         expired.
     */
    public String extendToken(String token) {
        LOGGER.debug("extendToken({}) - (ENTER)", token);

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
     * @return the name of the user, or null if the provided input was invalid or expired.
     */
    public String parseToken(String token) {
        LOGGER.debug("parseToken({}) - (ENTER)", token);

        String ret = null;

        Claims claims = parseClaims(token);
        if (claims != null) {
            ret = (String) claims.get(USER_NAME);
        }

        LOGGER.debug("parseToken() - (LEAVE)");
        return ret;
    }

    @SuppressWarnings("unchecked")
    public List<String> parseTokenFeatures(String token) {
        LOGGER.debug("parseToken({}) - (ENTER)", token);

        List<String> ret = null;

        Claims claims = parseClaims(token);
        if (claims != null) {
            ret = claims.get(FEATURES, List.class);
        }

        LOGGER.debug("parseToken() - (LEAVE)");
        return ret;
    }

    private String signClaims(Claims claims) {
        // Header
        Map<String, Object> header = new HashMap<>();
        header.put(Header.TYPE, Header.JWT_TYPE);
        header.put(JwsHeader.ALGORITHM, signatureAlgorithm);

        // Signature key
        SecretKey key = Keys.hmacShaKeyFor(getSecretKey());

        return Jwts.builder().setHeader(header).setClaims(claims).signWith(key, signatureAlgorithm).compact();
    }

    private Claims parseClaims(String token) {
        Claims ret = null;

        if (token != null && !token.trim().isEmpty()) {
            try {
                SecretKey key = Keys.hmacShaKeyFor(getSecretKey());
                ret = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            } catch (ExpiredJwtException e) {
                LOGGER.error("Token expired", e);
            } catch (UnsupportedJwtException | MalformedJwtException | SecurityException
                    | IllegalArgumentException e) {
                LOGGER.error("Failed to parse token", e);
            }
        }

        return ret;
    }

    private String genkey() {
        SecretKey generatedKey = Keys.secretKeyFor(signatureAlgorithm);
        return Base64.getEncoder().encodeToString(generatedKey.getEncoded());
    }

    private void initKey() {
        // get the key from properties if set
        String key = System.getProperty(SYSTEM_KEY);
        String jndikey = "USM/" + PROP_KEY;
        if (key == null || key.isEmpty()) {
            LOGGER.debug("no key defined in system property {}. Checking in properties file", SYSTEM_KEY);
            key = properties.getProperty(PROP_KEY);
            if (key == null || key.isEmpty()) {
                LOGGER.debug("no key defined in property. Checking in JNDI context");
                // let's look if a key exist in JNDI
                try {
                    key = JndiUtil.lookup(jndikey).toString();
                    LOGGER.debug("Found Key bound to JNDI name {} with value {}", jndikey, key);
                } catch (Exception e) {
                    LOGGER.debug("No secret key bound to JNDI name {}", jndikey);
                }
                if (key == null || key.isEmpty()) {
                    LOGGER.debug("no key in JNDI context. Generating a random one");
                    key = generateAndBindKey(jndikey);
                }
            }
        }
        LOGGER.debug("Secret Key set to: {}", key);

        secretKey = Base64.getDecoder().decode(key);
        LOGGER.debug("parsed Base64 key: {}", secretKey);
    }

    private String generateAndBindKey(String jndikey) {
        String key;
        // get a random key
        try {
            key = genkey();
            JndiUtil.createJNDI(jndikey, key);
            LOGGER.debug("Key now bound to JNDI name {}", jndikey);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("Error Generating secret key. Using default value", e);
            key = DEFAULT_KEY;
            JndiUtil.createJNDI(jndikey, key);
            LOGGER.debug("Key now bound to JNDI name {}", jndikey);
        }
        return key;
    }

    private void removeKey() {
        // get the key from properties if set
        String key = properties.getProperty(PROP_KEY);
        String jndikey = "USM/" + PROP_KEY;
        // let's look if a key exist in JNDI
        try {
            key = (String) JndiUtil.lookup(jndikey);
            LOGGER.debug("Key bound to JNDI name {}", PROP_KEY);
            try {
                JndiUtil.unbind(jndikey);
                LOGGER.debug("Unbound JNDI name {}", PROP_KEY);
            } catch (Exception e) {
                LOGGER.error("Error unbinding key", e);
            }

        } catch (Exception e) {
            LOGGER.error("Error looking up key", e);
        }

        secretKey = null;
    }

    private byte[] getSecretKey() {
        return secretKey;
    }
}
