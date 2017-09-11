package eu.europa.ec.mare.usm.information.rest.service;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Test;

public class DomainTrustManagerTest {

	static {
		TrustManager[] trustAllCerts = new TrustManager[] { 
			    new X509TrustManager() {     
			        public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
			            return new X509Certificate[0];
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
			    SSLContext sc = SSLContext.getInstance("SSL"); 
			    sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
			    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (GeneralSecurityException e) {
		} 
	}

	@Test
	public void httpsConnectTest() throws Exception {
		InputStream is = getClass().getResourceAsStream("/test.properties");
		Properties props = new Properties();
		props.load(is);
		String endPoint = props.getProperty("rest.endpoint");
		try { 
		    URL url = new URL(endPoint);
		    
		} catch (MalformedURLException e) {
		} 
	}
	
}
