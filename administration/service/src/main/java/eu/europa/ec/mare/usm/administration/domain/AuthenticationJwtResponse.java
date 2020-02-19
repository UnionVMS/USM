package eu.europa.ec.mare.usm.administration.domain;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;

public class AuthenticationJwtResponse extends AuthenticationResponse {

	private String jwtoken;
	private String ip;
	private String sessionId;
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getJwtoken() {
		return jwtoken;
	}

	public void setJwtoken(String jwtoken) {
		this.jwtoken = jwtoken;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		return "AuthenticationJwtResponse [JWToken=" + jwtoken + ", ip=" + ip
				+ ", sessionId=" + sessionId + "]";
	}

	
}
