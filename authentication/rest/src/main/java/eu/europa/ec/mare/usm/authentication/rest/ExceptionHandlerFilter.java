package eu.europa.ec.mare.usm.authentication.rest;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filters incoming REST requests, logging and reporting in JSON 
 * format, otherwise un-handled exceptions.
 */
@WebFilter(filterName = "ExceptionHandlerFilter", urlPatterns = {"/rest/*"})
public class ExceptionHandlerFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerFilter.class);
  private static final int INTERNAL_SERVER_ERROR = 500;

  /**
   * Creates a new instance
   */
  public ExceptionHandlerFilter() {
  }

  /**
   * Filters an incoming request, logging and reporting any un-handled
   * exception
   *
   * @param request The request we are processing
   * @param response The response we are creating
   * @param chain The filter chain we are processing
   *
   * @exception IOException if an input/output error occurs
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain)
  throws IOException
  {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    LOGGER.info("doFilter(" + httpRequest.getMethod() + ", "
            + httpRequest.getPathInfo() + ") - (ENTER)");

    try {
      chain.doFilter(request, response);
    } catch (Exception exc) {
      //  Log error
      LOGGER.error("Internal Server Error: " + exc.getMessage(), exc);

      //  Report (root cause of) error
      Throwable root = getRootCause(exc);
      String message = getExceptionName(root);
      if (root.getMessage() != null) {
        message = message + ": " + root.getMessage();
      }
      httpResponse.setContentType("application/json");
      httpResponse.setStatus(INTERNAL_SERVER_ERROR);
      StringBuilder sb = new StringBuilder();
      sb.append("{\"statusCode\":").
         append(INTERNAL_SERVER_ERROR).
         append(",\"message\":").
         append(quote(message)).
         append("}");
      httpResponse.getWriter().print(sb.toString());
    }

    LOGGER.info("doFilter() - (LEAVE)");
  }

  private String getExceptionName(Throwable e) 
  {
    String clazz = e.getClass().getName();
    
    int i = clazz.lastIndexOf('.');
    return clazz.substring(i+1);
  }
  
  public static String quote(String src) 
  {
    StringBuilder sb = new StringBuilder();

    sb.append('"');
    if (src != null) {
      for (int i = 0; i < src.length(); i++) {
        char c = src.charAt(i);

        switch (c) {
          case '\\':
          case '"':
            sb.append('\\');
            sb.append(c);
            break;
          case '/':
            sb.append('\\');
            sb.append(c);
            break;
          case '\b':
            sb.append("\\b");
            break;
          case '\t':
            sb.append("\\t");
            break;
          case '\n':
            sb.append("\\n");
            break;
          case '\f':
            sb.append("\\f");
            break;
          case '\r':
            sb.append("\\r");
            break;
          default:
            if (c < ' ') {
              String t = "000" + Integer.toHexString(c);
              sb.append("\\u").append(t.substring(t.length() - 4));
            } else {
              sb.append(c);
            }
        }
      }
    }
    sb.append('"');
    
    return sb.toString();
  }

  private static Throwable getRootCause(Throwable src)
  {
    Throwable ret = src;
    
    if (src != null && src.getCause() != null) {
      ret = getRootCause(src.getCause());
    }
    
    return ret;
  }
  
  @Override
  public void init(FilterConfig fc) {
    // NOP
  }

  @Override
  public void destroy() {
    // NOP
  }

}
