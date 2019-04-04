package eu.europa.ec.mare.usm.administration.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Computes hash of passwords
 */
public class PasswordDigester {
  private static final String MD5 = "MD5";

  
  /**
   * Computes the MD5 hash of the provided password.
   * 
   * @param password the password
   * 
   * @return the computed hash as an hexadecimal string
   * 
   * @throws RuntimeException in case the MD5 algorithm is not supported  
   */
  public String hashPassword(String password)
  {
    String ret = null;
    
    if (password != null) {
      try {
        MessageDigest md = MessageDigest.getInstance(MD5);
        md.update(password.getBytes());
        byte digest[] = md.digest();
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
          sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        ret = sb.toString();
      } catch (NoSuchAlgorithmException ex) {
        throw new RuntimeException("Failed to compute hash", ex);
      }
    }
    
    return ret;
  }
  
}
