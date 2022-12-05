package com.ventura.panverification.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ventura.panverification.util.ApplicationConstants;

/**
 * This is filter component class, every request to api will go through this
 * filter class. This class checks request contains valid header value.
 * 
 * @author Extrapreneurs India
 * @version 1.0
 */
@Component
public class AuthenticationFilter implements Filter {

	@Autowired
	private Environment env;

	/**
	 * This dofilter method checks request contains valid header value. if header
	 * values is not matched then throw the error.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;

		String headerKey = req.getHeader(ApplicationConstants.HEADER_KEY);
		String headerVal = env.getProperty(ApplicationConstants.HEADER_KEY);

		if (headerKey == null || !headerKey.equals(headerVal)) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The header key is not valid.");
		} else {
			chain.doFilter(request, response);
		}
	}

}
