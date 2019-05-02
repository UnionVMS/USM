package eu.europa.ec.mare.usm.jwt;

import java.util.List;
import javax.ejb.Lock;
import javax.ejb.LockType;

public interface JwtTokenHandler {
  @Lock(LockType.READ)
  public String createToken(String userName);
  @Lock(LockType.READ)
  public String createToken(String userName, List<String> features);
  @Lock(LockType.READ)
  public String extendToken(String token);
  @Lock(LockType.READ)
  public String parseToken(String token);
  @Lock(LockType.READ)
  public List<String> parseTokenFeatures(String token);
  
}
