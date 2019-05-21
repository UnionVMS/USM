package eu.europa.ec.mare.usm.jwt;

import java.util.List;
import javax.ejb.Lock;
import javax.ejb.LockType;

public interface JwtTokenHandler {

  @Lock(LockType.READ)
  String createToken(String userName);

  @Lock(LockType.READ)
  String createToken(String userName, List<Integer> features);

  @Lock(LockType.READ)
  String extendToken(String token);

  @Lock(LockType.READ)
  String parseToken(String token);

  @Lock(LockType.READ)
  List<Integer> parseTokenFeatures(String token);
  
}
