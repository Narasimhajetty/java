package com.ventura.panverification.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ventura.panverification.model.Pan;
import com.ventura.panverification.service.PanVerificationService;

/**
 * This is Rest controller class which has rest api's exposed to verify PAN
 * number
 * 
 * @author Extrapreneurs India
 * @version 1.0
 */
@RestController
@RequestMapping("/v1/auth")
public class PanVerificationController {

	private static final Logger logger = LogManager.getLogger(PanVerificationController.class);

	@Autowired
	private PanVerificationService panVerificationService;

	/**
	 * This method returns the NSDL response about pan information based on input
	 * PAN.
	 * 
	 * @param PAN The PAN value to be verify.
	 * @return NSDL Response about PAN.
	 */
	@PostMapping("/pan")
	public ResponseEntity<?> verifyPan(@RequestBody Pan pan) throws Exception {
		logger.info("PAN Verification started for PAN:{}", pan.getPan());
		Pan panResp = null;
		if (pan.getPan() != null && pan.getPan().length() == 10) {
			String nsdlresp = panVerificationService.verifyPan(pan.getPan());
			panResp = panVerificationService.parseNsdlResponse(nsdlresp);
		} else {
			panResp = panVerificationService.prepareErrorResponse();
		}
		logger.info("PAN Verification completed for PAN:{}", pan.getPan());
		return new ResponseEntity<>(panResp, HttpStatus.OK);
	}

}
