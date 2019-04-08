package eu.europa.ec.mare.usm.administration.cas;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.authentication.AuthenticationRedirectStrategy;

public class CASAuthenticationRedirectStrategy implements AuthenticationRedirectStrategy {

	@Override
	public void redirect(HttpServletRequest request, HttpServletResponse response, String potentialRedirectUrl)
			throws IOException {

		if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
			response.setContentType("application/json");
			response.setStatus(200);

			final PrintWriter writer = response.getWriter();
			writer.write("{ \"success\" : false, \"status\" : \"CAS_AUTHENTICATION_REQUIRED\", \"code\" : 303, \"message\" : \"session expired\" }");
		} else {
			response.sendRedirect(potentialRedirectUrl);
		}

	}
}
