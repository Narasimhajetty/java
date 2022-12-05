package com.ventura.panverification.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * PanVerificationServiceApplication Class that can be used to bootstrap and
 * launch a Login micro-service from a Java main method
 * 
 * @author EP
 * @version 1.0
 * @since 2022-04-02
 */
@EnableAsync
@SpringBootApplication(scanBasePackages = { "com.ventura" })
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@OpenAPIDefinition(info = @Info(title = "PAN Verification Micro-Service", version = "1.0", description = "PAN Verification Portal Service"))
public class PanVerificationServiceApplication {

	/**
	 * Launches the UI Portal service application
	 * 
	 * @param args - Application startup arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PanVerificationServiceApplication.class, args);
	}

}
