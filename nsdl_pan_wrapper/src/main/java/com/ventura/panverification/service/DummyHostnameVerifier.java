package com.ventura.panverification.service;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class DummyHostnameVerifier implements HostnameVerifier {

	public boolean verify(String urlHostname, String certHostname) {
		return true;
	}

	public boolean verify(String arg0, SSLSession arg1) {
		return true;
	}
}
