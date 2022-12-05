package com.ventura.panverification.service;

import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.ventura.panverification.model.NsdlApiErrorCodeEnum;
import com.ventura.panverification.model.Pan;
import com.ventura.panverification.model.PanStatusCodeEnum;
import com.ventura.panverification.util.ApplicationConstants;

/**
 * This class demonstrates PAN verification methods.
 * 
 * @author Extrapreneurs India
 * @version 1.0
 */
@Service
public class PanVerificationService {

	private static final Logger logger = LogManager.getLogger(PanVerificationService.class);

	@Autowired
	private Environment env;

	@Autowired
	private NSDLClient nsdlClient;

	@Autowired
	private SignatureService signatureService;

	/**
	 * This method creates data required for NSDL and verify the PAN by calling NSDL
	 * API's.
	 * 
	 * @param PAN The PAN value to be verify.
	 * @return NSDL Response about PAN.
	 */
	public String verifyPan(String pan) {
		String resp = null;
		String venturaId = env.getProperty("panservice.nsdl.api.venturaId");
		pan = venturaId + pan;
		byte[] signByteData = signatureService.generateSignature(pan);

		if (signByteData != null) {
			String signature = new String(signByteData);
			resp = nsdlClient.callNsdlApi(signature, pan);
		}
		return resp;
	}

	/**
	 * This method Parse NSDL API's response.
	 * 
	 * @param Response NSDL Response.
	 * @return PAN PAN Object.
	 */
	public Pan parseNsdlResponse(String nsdlresp) {
		logger.info("parse nsdl response");
		String[] resp = nsdlresp.split(Pattern.quote("^"));
		Pan panResp = new Pan();

		panResp.setReturnCode(resp.length>0 ? getErrorCodeDesp(resp[0]) : null);
		panResp.setPan(1 < resp.length ? resp[1] : null);
		panResp.setPanStatus(2 < resp.length ? getPanStatusDesp(resp[2]) : null);
		panResp.setLastName(3 < resp.length ? resp[3] : null);
		panResp.setFirstName(4 < resp.length ? resp[4] : null);
		panResp.setMiddleName(5 < resp.length ? resp[5] : null);
		panResp.setSalutation(6 < resp.length ? resp[6] : null);
		panResp.setLastUpdateDate(7 < resp.length ? resp[7] : null);
		panResp.setCardName(8 < resp.length ? resp[8] : null);
		panResp.setAadharStatus(9 < resp.length ? getAadharStatusDesp(resp[9]) : null);
		logger.info("parsed nsdl response successfully.");
		return panResp;
	}

	/**
	 * This method set NSDL API's error code response.
	 * 
	 * @param respCode NSDL Response code.
	 * @return String error description.
	 */
	private String getErrorCodeDesp(String respCode) {
		String desp = null;
		switch (respCode) {
		case "1":
			desp = NsdlApiErrorCodeEnum.E1.getErrorDescription();
			break;
		case "2":
			desp = NsdlApiErrorCodeEnum.E2.getErrorDescription();
			break;
		case "3":
			desp = NsdlApiErrorCodeEnum.E3.getErrorDescription();
			break;
		case "4":
			desp = NsdlApiErrorCodeEnum.E4.getErrorDescription();
			break;
		case "5":
			desp = NsdlApiErrorCodeEnum.E5.getErrorDescription();
			break;
		case "6":
			desp = NsdlApiErrorCodeEnum.E6.getErrorDescription();
			break;
		case "7":
			desp = NsdlApiErrorCodeEnum.E7.getErrorDescription();
			break;
		case "8":
			desp = NsdlApiErrorCodeEnum.E8.getErrorDescription();
			break;
		case "9":
			desp = NsdlApiErrorCodeEnum.E9.getErrorDescription();
			break;
		case "10":
			desp = NsdlApiErrorCodeEnum.E10.getErrorDescription();
			break;
		case "11":
			desp = NsdlApiErrorCodeEnum.E11.getErrorDescription();
			break;
		case "12":
			desp = NsdlApiErrorCodeEnum.E12.getErrorDescription();
			break;
		default:
			desp = "Code not found";
			break;

		}
		return desp;
	}

	/**
	 * This method set and return NSDL API's PAN status code response.
	 * 
	 * @param panStatusCode NSDL PAN status Response code.
	 * @return String PAN status description.
	 */
	private String getPanStatusDesp(String panStatusCode) {
		String desp = null;
		switch (panStatusCode) {
		case "E":
			desp = PanStatusCodeEnum.E.getStatusDescription();
			break;
		case "F":
			desp = PanStatusCodeEnum.F.getStatusDescription();
			break;
		case "X":
			desp = PanStatusCodeEnum.X.getStatusDescription();
			break;
		case "D":
			desp = PanStatusCodeEnum.D.getStatusDescription();
			break;
		case "N":
			desp = PanStatusCodeEnum.N.getStatusDescription();
			break;
		case "EA":
			desp = PanStatusCodeEnum.EA.getStatusDescription();
			break;
		case "EC":
			desp = PanStatusCodeEnum.EC.getStatusDescription();
			break;
		case "ED":
			desp = PanStatusCodeEnum.ED.getStatusDescription();
			break;
		case "EI":
			desp = PanStatusCodeEnum.EI.getStatusDescription();
			break;
		case "EL":
			desp = PanStatusCodeEnum.EL.getStatusDescription();
			break;
		case "EM":
			desp = PanStatusCodeEnum.EM.getStatusDescription();
			break;
		case "EP":
			desp = PanStatusCodeEnum.EP.getStatusDescription();
			break;
		case "ES":
			desp = PanStatusCodeEnum.ES.getStatusDescription();
			break;
		case "EU":
			desp = PanStatusCodeEnum.EU.getStatusDescription();
			break;
		default:
			desp = "PAN Status Code not found";
			break;
		}
		return desp;
	}

	/**
	 * This method set and return NSDL API's PAN AADHAR status code response.
	 * 
	 * @param aadharStatusCode NSDL PAN AADHAR status Response code.
	 * @return String PAN AADHAR status description.
	 */
	private String getAadharStatusDesp(String aadharStatusCode) {
		String aadharDesp = null;
		switch (aadharStatusCode) {
		case "Y":
			aadharDesp = ApplicationConstants.AADHAR_STATUS_FOR_Y;
			break;
		case "R":
			aadharDesp = ApplicationConstants.AADHAR_STATUS_FOR_R;
			break;
		default:
			aadharDesp = ApplicationConstants.AADHAR_STATUS_NOT_FOUND;
			break;
		}
		return aadharDesp;
	}

	/**
	 * This method set error response if pan is invalid.
	 * 
	 * @return String PAN Object with error status as description.
	 */
	public Pan prepareErrorResponse() {
		Pan panResp = new Pan();
		panResp.setReturnCode(ApplicationConstants.SUCCESS);
		panResp.setPanStatus(ApplicationConstants.INVALID_PAN);
		return panResp;
	}
}
