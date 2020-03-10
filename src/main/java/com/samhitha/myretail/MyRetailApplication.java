package com.samhitha.myretail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;
import java.time.Duration;

@SpringBootApplication
public class MyRetailApplication
{

	public static void main(String[] args) {
		SpringApplication.run(MyRetailApplication.class, args);
	}

	@Bean
	public HttpClient httpClient() {
		return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(120L)).build();
	}

}
