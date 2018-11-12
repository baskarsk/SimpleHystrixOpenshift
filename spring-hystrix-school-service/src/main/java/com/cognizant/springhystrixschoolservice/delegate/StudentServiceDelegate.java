package com.cognizant.springhystrixschoolservice.delegate;

import java.util.Date;

import java.lang.System;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;



@Service
public class StudentServiceDelegate {
	@Autowired
	RestTemplate restTemplate;
	
	@HystrixCommand(fallbackMethod = "callStudentServiceAndGetData_Fallback")
	public String callStudentServiceAndGetData(String schoolname) {
		System.out.println("Getting School details for " + schoolname);
		String baseUrl = "";
		
		if (!isEmpty(System.getenv("STUDENT_APP_SERVICE_HOST")) // check kubernetes service 
				&& !isEmpty(System.getenv("STUDENT_APP_SERVICE_PORT")))
			baseUrl = "http://" + System.getenv("STUDENT_APP_SERVICE_HOST") + ":" + System.getenv("STUDENT_APP_SERVICE_PORT");
		else
			if (isEmpty(baseUrl)) { // default value
				baseUrl = "http://localhost:8098";
			}
			
		
//		String response = restTemplate
//				.exchange("http://{server.host}:8098/getStudentDetailsForSchool/{schoolname}"
//				, HttpMethod.GET
//				, null
//				, new ParameterizedTypeReference<String>() {
//			}, schoolname).getBody();
		
		String response = restTemplate
				.exchange(baseUrl+"/getStudentDetailsForSchool/{schoolname}"
				, HttpMethod.GET
				, null
				, new ParameterizedTypeReference<String>() {
			}, schoolname).getBody();

		System.out.println("Response Received as " + response + " -  " + new Date());

		return "NORMAL FLOW !!! - School Name -  " + schoolname + " :::  Student Details " + response + " -  " + new Date();
	}
	
	@SuppressWarnings("unused")
	private String callStudentServiceAndGetData_Fallback(String schoolname) {
		System.out.println("Student Service is down!!! fallback route enabled...");
		return "CIRCUIT BREAKER ENABLED!!!No Response From Student Service at this moment. Service will be back shortly - " + new Date();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
}
