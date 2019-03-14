package eu.europa.ec.mare.usm.jwt;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;


public interface JwtTokenHandler {
  @Lock(LockType.READ)
  public String createToken(String userName);
  @Lock(LockType.READ)
  public String extendToken(String token);
  @Lock(LockType.READ)
  public String parseToken(String token);
  
}
