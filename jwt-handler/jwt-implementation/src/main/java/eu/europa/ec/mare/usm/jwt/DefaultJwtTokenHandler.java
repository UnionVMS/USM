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
    private static final String SIGNATURE_KEY_PROPERTY_NAME = "secretKey";
    private static final String PROP_SUBJECT = "subject";
    private static final String PROP_ISSUER = "issuer";
    private static final String PROP_ID = "id";
    private static final long DEFAULT_TTL = 8 * 60 * 60 * 1000;
    private static final String TTL_PROPERTY_NAME = "timeToLiveInMinutes";
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

    public String createToken(String userName, List<Integer> features) {
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
            claims.setExpiration(new Date(now + getTtlInMilliseconds()));

            ret = signClaims(claims);
        }

        LOGGER.debug("extendToken() - (LEAVE)");
        return ret;
    }

    private long getTtlInMilliseconds() {
        String configValue = getConfigValue(TTL_PROPERTY_NAME);
        if (configValue != null && !configValue.isEmpty()) {
            try {
                long ttlInMinutes = Long.parseLong(configValue);
                long ttlInMilliseconds = ttlInMinutes * 60 * 1000;
                if (ttlInMilliseconds > 0) {
                    return ttlInMilliseconds;
                }
                LOGGER.warn("Configured TTL value is not positive: {}", ttlInMinutes);
            } catch (NumberFormatException e) {
                LOGGER.warn("Failed to parse TTL config value to number: {}", configValue);
            }
        }

        return DEFAULT_TTL;
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
    public List<Integer> parseTokenFeatures(String token) {
        LOGGER.debug("parseToken({}) - (ENTER)", token);

        List<Integer> ret = null;

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
        String base64signatureKey = getConfigValue(SIGNATURE_KEY_PROPERTY_NAME);
        if (base64signatureKey == null || base64signatureKey.isEmpty()) {
            LOGGER.debug("No secret JWT signature key found. Generating a random one");
            base64signatureKey = generateAndBindKey("USM/" + SIGNATURE_KEY_PROPERTY_NAME);
        }

        LOGGER.debug("Secret JWT signature key set to: {}", base64signatureKey);

        secretKey = Base64.getDecoder().decode(base64signatureKey);
        LOGGER.debug("Parsed Base64 JWT signature key: {}", secretKey);
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
        String key = properties.getProperty(SIGNATURE_KEY_PROPERTY_NAME);
        String jndikey = "USM/" + SIGNATURE_KEY_PROPERTY_NAME;
        // let's look if a key exist in JNDI
        try {
            key = (String) JndiUtil.lookup(jndikey);
            LOGGER.debug("Key bound to JNDI name {}", SIGNATURE_KEY_PROPERTY_NAME);
            try {
                JndiUtil.unbind(jndikey);
                LOGGER.debug("Unbound JNDI name {}", SIGNATURE_KEY_PROPERTY_NAME);
            } catch (Exception e) {
                LOGGER.error("Error unbinding key", e);
            }

        } catch (Exception e) {
            LOGGER.error("Error looking up key", e);
        }

        secretKey = null;
    }

    private String getConfigValue(String key) {
        // get the value from properties if set
        String value = System.getProperty("USM." + key);

        if (value != null && !value.isEmpty()) {
            LOGGER.debug("{} found in system properties.", key);
            return value;
        }
        LOGGER.debug("{} not found in system properties. Checking in properties file.", key);

        value = properties.getProperty(key);
        if (value != null && !value.isEmpty()) {
            LOGGER.debug("{} found in JWT properties file.", key);
            return value;
        }
        LOGGER.debug("{} not found in JWT properties file. Checking in JNDI context.", key);

        String jndikey = "USM/" + key;
        Object lookup = JndiUtil.lookup(jndikey);
        if (lookup != null && !lookup.toString().isEmpty()) {
            value = lookup.toString();
            LOGGER.debug("Found JNDI entry {} with value {}", jndikey, value);
        } else {
            LOGGER.debug("No JNDI entry {} found", jndikey);
        }

        return value;
    }

    private byte[] getSecretKey() {
        return secretKey;
    }

    public static long getDefaultTtl() {
        return DEFAULT_TTL;
    }
}
