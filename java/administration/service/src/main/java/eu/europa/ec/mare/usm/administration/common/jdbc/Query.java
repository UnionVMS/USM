package eu.europa.ec.mare.usm.administration.common.jdbc;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds an SQL query expressed as a prepared-statement and its 
 * bind parameters.
 */
public class Query {
  private final StringBuilder statement = new StringBuilder();
  private final List parameters = new ArrayList();

  /**
   * Creates a new instance.
   */
  public Query()
  {
  }

  /**
   * Creates a new instance with an initial SQL statement/fragment.
   * 
   * @param sql the initial SQL statement/fragment
   */
  public Query(String sql)
  {
    statement.append(sql);
  }

  /**
   * Gets the SQL prepared-statement.
   * 
   * @return the SQL prepared-statement
   */
  public String getStatement()
  {
    return statement.toString();
  }

  /**
   * Appends the provided SQL statement/fragment to the prepared-statement.
   * 
   * @param sql the SQL statement/fragment to be appended
   * 
   * @return this instance
   */
  public Query append(String sql)
  {
    statement.append(sql);
    
    return this;
  }

  /**
   * Appends the provided Character to the prepared-statement.
   * 
   * @param c the Character to be appended
   * 
   * @return this instance
   */
  public Query append(char c)
  {
    statement.append(c);
    
    return this;
  }

  /**
   * Gets the list of bind parameters.
   * 
   * @return the possibly empty list of parameters
   */
  public List getParameters()
  {
    return parameters;
  }
  
  /**
   * Adds the provided parameter to the list of bind parameters.
   * 
   * @param parameter the parameter to be added
   * 
   * @return this instance
   */
  public Query add(Object parameter)
  {
    parameters.add(parameter);
    
    return this;
  }

  /**
   * Closes unbalanced parenthesis.
   */
  public void closeParenthesis()
  {
    int cnt = 0;
    
    for (int i = 0; i < statement.length(); i++) {
      char c = statement.charAt(i);
      if (c == '(') {
        cnt += 1;
      } else if (c == ')') {
        cnt -= 1;
      }
    }        
    for (int i = 0; i < cnt; i++) {
      statement.append(')');
    }
  }
  
  
  /**
   * Formats a human-readable view of this instance.
   * 
   * @return the human-readable view
   */
  public String toString()
  {
    return "{" + statement.toString() + ", " + parameters + "}";
  }
}
