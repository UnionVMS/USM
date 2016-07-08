/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
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