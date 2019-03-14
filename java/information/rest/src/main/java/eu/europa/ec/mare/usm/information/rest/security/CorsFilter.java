package eu.europa.ec.mare.usm.information.rest.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filters incoming REST requests, setting Access-Control-Allow headers
 * for allowing Cross-site HTTP requests.
 */
@WebFilter(filterName = "CorsFilter", urlPatterns = {"/rest/*"})
public class CorsFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);

  /**
   * Creates a new instance
   */
  public CorsFilter() {
  }

  /**
   * Filters an incoming request, adding Access-Control-Allow headers
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
  throws IOException, ServletException
  {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    LOGGER.info("doFilter(" + httpRequest.getMethod() + ", "
            + httpRequest.getPathInfo() + ") - (ENTER)");

    httpResponse.setHeader("Access-Control-Allow-Origin", "*");
    httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
    httpResponse.setHeader("Access-Control-Allow-Headers", httpRequest.getHeader("Access-Control-Request-Headers"));
    chain.doFilter(httpRequest, httpResponse);

    LOGGER.info("doFilter() - (LEAVE)");
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
