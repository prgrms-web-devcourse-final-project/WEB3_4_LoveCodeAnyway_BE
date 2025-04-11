package com.ddobang.backend.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@Configuration
public class AppConfig {
	@Getter
	private static ObjectMapper objectMapper;

	@Autowired
	public void setObjectMapper(ObjectMapper objectMapper) {
		AppConfig.objectMapper = objectMapper;
	}
}
