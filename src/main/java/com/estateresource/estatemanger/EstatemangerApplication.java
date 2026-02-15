package com.estateresource.estatemanger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
		exclude = {
				DataSourceAutoConfiguration.class
		}
)
@EnableFeignClients
@EnableScheduling
public class EstatemangerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EstatemangerApplication.class, args);
	}

}
