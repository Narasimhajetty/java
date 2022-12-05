package com.ventura.panverification.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * This class generates the signature data for PAN
 * 
 * @author Extrapreneurs India
 * @version 1.0
 */
@Service
public class SignatureService {

	private static final Logger logger = LogManager.getLogger(SignatureService.class);

	@Autowired
	private Environment env;

	/**
	 * This method generate signature data required for NSDL using PAN.
	 * 
	 * @param PAN The PAN value to be verify.
	 * @return BYTE[] Signature data of PAN.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public byte[] generateSignature(String data) {
		logger.info("Generate Signature data for PAN:{}", data);
		String jksKeyStoreFile = "output.jks";//env.getProperty("panservice.jksKeyStore.file.path");
		String keyStorePwd = env.getProperty("panservice.jksKeyStore.file.password");

		logger.info("jksKeyStoreFile:{}", jksKeyStoreFile);
		
		try {
			//final KeyStore keystore = KeyStore.getInstance("PKCS12");
			final KeyStore keystore = KeyStore.getInstance("JKS");
			//final InputStream input = new FileInputStream(jksKeyStoreFile);
			final InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(jksKeyStoreFile);
			try {
				final char[] password = keyStorePwd.toCharArray();
				logger.info("jksKeyStoreFile load:{}", input);
				keystore.load(input, password);
			} catch (IOException ex) {
				logger.error("error occurred while loading jksKeyStoreFile");
				logger.error(ex);
			}
			final Enumeration e2 = keystore.aliases();
			String alias = "";
			if (e2 != null) {
				while (e2.hasMoreElements()) {
					final String n = (String) e2.nextElement();
					if (keystore.isKeyEntry(n)) {
						alias = n;
					}
				}
			}
			final PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, keyStorePwd.toCharArray());
			final X509Certificate myPubCert = (X509Certificate) keystore.getCertificate(alias);
			final byte[] dataToSign = data.getBytes();
			CMSSignedDataGenerator sgen = null;
			try {
				sgen = new CMSSignedDataGenerator();
			} catch (Exception ex) {
				logger.error("SignatureGeneration: {0}", ex);
			}
			Security.addProvider((Provider) new BouncyCastleProvider());
//			SignerInformation signInfo = new SignerInformation
//			sgen.addSigners(new SignerInformationStore());
//			sgen.addSigner(privateKey, myPubCert, CMSSignedDataGenerator.DIGEST_SHA1);
			
			ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privateKey);
			sgen.addSignerInfoGenerator(
	                new JcaSignerInfoGeneratorBuilder(
	                     new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
	                     .build(sha1Signer, myPubCert));
			
			final Certificate[] certChain = keystore.getCertificateChain(alias);
			final ArrayList certList = new ArrayList();
			final CertStore certs = null;
			for (int i = 0; i < certChain.length; ++i) {
				certList.add(certChain[i]);
			}
//			sgen.addCertificatesAndCRLs(
//					CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), "BC"));
			sgen.addCertificates(new JcaCertStore(Arrays.asList(new CollectionCertStoreParameters(certList))));
			
//			List certList1 = new ArrayList();
//			certList1.add(myPubCert);
			//Store certs1 = new JcaCertStore(certList);
			CMSTypedData msg = new CMSProcessableByteArray(data.getBytes());

			final CMSSignedData csd = sgen.generate(msg, false);
			
//			final CMSSignedData csd = sgen.generate((CMSProcessable) new CMSProcessableByteArray(dataToSign), true);
			
			
			final byte[] signedData = csd.getEncoded();
			final byte[] signedData2 = Base64.encode(signedData);
			logger.info("Signature data generated successfully for PAN:{}", data);
			return signedData2;
		} catch (Exception e3) {
			logger.error("Error occurred while generating signature data:{0}", e3);
			return null;
		}
	}
}