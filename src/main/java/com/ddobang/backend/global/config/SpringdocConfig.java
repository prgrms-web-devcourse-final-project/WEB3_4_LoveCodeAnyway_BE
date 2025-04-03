package com.ddobang.backend.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SpringdocConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("또방 API 명세서")
				.description("또방 프로젝트의 백엔드 REST API 문서입니다.")
				.version("v1.0.0"));
	}

	@Bean
	public GroupedOpenApi allApi() {
		return GroupedOpenApi.builder()
			.group("allApi")
			.pathsToMatch("/**")
			.build();
	}

	@Bean
	public GroupedOpenApi alarmApi() {
		return GroupedOpenApi.builder()
			.group("alarmApi")
			.pathsToMatch("/api/v1/alarms/**")
			.build();
	}

	@Bean
	public GroupedOpenApi boardApi() {
		return GroupedOpenApi.builder()
			.group("boardApi")
			.pathsToMatch("/api/v1/boards/**")
			.build();
	}

	@Bean
	public GroupedOpenApi diaryApi() {
		return GroupedOpenApi.builder()
			.group("diaryApi")
			.pathsToMatch("/api/v1/diaries/**")
			.build();
	}

	@Bean
	public GroupedOpenApi memberApi() {
		return GroupedOpenApi.builder()
			.group("memberApi")
			.pathsToMatch("/api/v1/members/**")
			.build();
	}

	@Bean
	public GroupedOpenApi messageApi() {
		return GroupedOpenApi.builder()
			.group("messageApi")
			.pathsToMatch("/api/v1/messages/**")
			.build();
	}

	@Bean
	public GroupedOpenApi partyApi() {
		return GroupedOpenApi.builder()
			.group("partyApi")
			.pathsToMatch("/api/v1/parties/**")
			.build();
	}

	@Bean
	public GroupedOpenApi regionApi() {
		return GroupedOpenApi.builder()
			.group("regionApi")
			.pathsToMatch("/api/v1/regions/**")
			.build();
	}

	@Bean
	public GroupedOpenApi storeApi() {
		return GroupedOpenApi.builder()
			.group("storeApi")
			.pathsToMatch("/api/v1/stores/**")
			.build();
	}

	@Bean
	public GroupedOpenApi themeApi() {
		return GroupedOpenApi.builder()
			.group("themeApi")
			.pathsToMatch("/api/v1/themes/**")
			.build();
	}

	@Bean
	public GroupedOpenApi adminApi() {
		return GroupedOpenApi.builder()
			.group("adminApi")
			.pathsToMatch("/api/v1/admin/**")
			.build();
	}
}
