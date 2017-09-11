package eu.europa.ec.mare.usm.information.rest.service;

import java.security.GeneralSecurityException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class DomainTrustManager {

	public static SSLContext sc;
	
	static {
		 HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){ 
             public boolean verify(String hostname, SSLSession session) { 
                     return true; 
             }}); 
		 
		TrustManager[] trustAllCerts = new TrustManager[] { 
			    new X509TrustManager() {     
			        public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
			            return null;
			        } 
			        public void checkClientTrusted( 
			            java.security.cert.X509Certificate[] certs, String authType) {
			            } 
			        public void checkServerTrusted( 
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			    } 
			}; 
			try {
			    sc = SSLContext.getInstance("TLS"); 
			    sc.init(null, trustAllCerts, new java.security.SecureRandom());
			    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (GeneralSecurityException e) {
		} 
	}
	
	public static SSLContext getSslContext() {
		return sc;
	}
	
}
