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
package eu.europa.ec.mare.usm.administration.domain;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;

public class AuthenticationJwtResponse extends AuthenticationResponse {

	private String JWToken;
	private String ip;
	private String sessionId;
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getJWToken() {
		return JWToken;
	}

	public void setJWToken(String jWToken) {
		JWToken = jWToken;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		return "AuthenticationJwtResponse [JWToken=" + JWToken + ", ip=" + ip
				+ ", sessionId=" + sessionId + "]";
	}

	
}