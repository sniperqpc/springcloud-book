package com.forezp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;


@SpringBootApplication
@EnableTurbineStream
public class MonitorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitorServiceApplication.class, args);
	}
}
