package com.pg.paymentgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentGatewayWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentGatewayWorkerApplication.class, args);
	}
//
//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//		return application.sources(PaymentGatewayWorkerApplication.class);
//	}

}
