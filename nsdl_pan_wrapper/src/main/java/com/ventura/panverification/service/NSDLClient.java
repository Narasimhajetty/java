package com.ventura.panverification.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.ventura.panverification.util.ApplicationConstants;

/**
 * This class demonstrates PAN verification methods calls nsdl api.
 * 
 * @author Extrapreneurs India
 * @version 1.0
 */
@Service
public class NSDLClient {

	private static final Logger logger = LogManager.getLogger(NSDLClient.class);

	@Autowired
	private Environment env;

	/**
	 * This method call NSDL API's for PAN verification.
	 * 
	 * @param Signature data The Byte array generated from PAN.
	 * @param PAN       The PAN value to be verify.
	 * @return BYTE[] Signature data of PAN.
	 */
	public String callNsdlApi(String signData, String data) {
		logger.info("Calling NSDL API for verification of PAN :{}", data);
		Calendar c = Calendar.getInstance();
		long nonce = c.getTimeInMillis();
		Date startTime = null;
		Calendar c1 = Calendar.getInstance();
		startTime = c1.getTime();
		SSLContext sslcontext = null;
		final String version = "2";
		String nsdlResp = null;
		String urlOfNsdl = env.getProperty("panservice.nsdl.api.url");

		try {
			sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(new KeyManager[0], new TrustManager[] { new DummyTrustManager() }, new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception: {} ::Program Start Time:{} ::nonce= {}", e.getMessage(), startTime, nonce);
		} catch (KeyManagementException e) {
			logger.error("Exception: {} ::Program Start Time:{} ::nonce= {}", e.getMessage(), startTime, nonce);
		}

		SSLSocketFactory factory = sslcontext.getSocketFactory();
		String urlParameters = "data=";
		try {
			urlParameters = urlParameters + URLEncoder.encode(data, "UTF-8") + "&signature="
					+ URLEncoder.encode(signData, "UTF-8");
		} catch (Exception e) {
			logger.error("Exception: {} ::Program Start Time:{} ::nonce= {}", e.getMessage(), startTime, nonce);
		}

		try {
			URL url;
			HttpsURLConnection connection;
			InputStream is = null;

			String ip = urlOfNsdl;
			url = new URL(ip);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod(ApplicationConstants.POST);
			connection.setRequestProperty(ApplicationConstants.CONTENT_TYPE, ApplicationConstants.CONTENT_TYPE_URLENCODED);
			connection.setRequestProperty(ApplicationConstants.CONTENT_LENGTH, "" + Integer.toString(urlParameters.getBytes().length));

			connection.setRequestProperty(ApplicationConstants.CONTENT_LANGUAGE, ApplicationConstants.CONTENT_LANGUAGE_VALUE);
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setSSLSocketFactory(factory);
			connection.setHostnameVerifier(new DummyHostnameVerifier());
			OutputStream os = connection.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(urlParameters);
			osw.flush();
			Date connectionStartTime = new Date();
			logger.info("::Request Sent At: {}", connectionStartTime);
			logger.info("::Request Data: {}", data);
			logger.info("::Version: {}", version);
			osw.close();
			is = connection.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));

			nsdlResp = in.readLine();

			logger.info("Received NSDL Response for PAN: {} as:: {}", data, nsdlResp);
			is.close();
			in.close();
		} catch (ConnectException e) {
			logger.error("Exception: {} ::Program Start Time:{} ::nonce= {}", e.getMessage(), startTime, nonce);
		} catch (Exception e) {
			logger.error("Exception: {} ::Program Start Time:{} ::nonce= {}", e.getMessage(), startTime, nonce);
			logger.error(e);
		}
		return nsdlResp;
	}
}
