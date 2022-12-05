package com.ventura.panverification.service;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class DummyTrustManager implements X509TrustManager {

	public DummyTrustManager() {
	}

	public boolean isClientTrusted(X509Certificate cert[]) {
		return true;
	}

	public boolean isServerTrusted(X509Certificate cert[]) {
		return true;
	}

	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

	public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

	}

	public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

	}
}
