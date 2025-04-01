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
			.pathsToMatch("/alarms/**")
			.build();
	}

	@Bean
	public GroupedOpenApi boardApi() {
		return GroupedOpenApi.builder()
			.group("boardApi")
			.pathsToMatch("/boards/**")
			.build();
	}

	@Bean
	public GroupedOpenApi diaryApi() {
		return GroupedOpenApi.builder()
			.group("diaryApi")
			.pathsToMatch("/diaries/**")
			.build();
	}

	@Bean
	public GroupedOpenApi memberApi() {
		return GroupedOpenApi.builder()
			.group("memberApi")
			.pathsToMatch("/members/**")
			.build();
	}

	@Bean
	public GroupedOpenApi messageApi() {
		return GroupedOpenApi.builder()
			.group("messageApi")
			.pathsToMatch("/messages/**")
			.build();
	}

	@Bean
	public GroupedOpenApi partyApi() {
		return GroupedOpenApi.builder()
			.group("partyApi")
			.pathsToMatch("/parties/**")
			.build();
	}

	@Bean
	public GroupedOpenApi regionApi() {
		return GroupedOpenApi.builder()
			.group("regionApi")
			.pathsToMatch("/regions/**")
			.build();
	}

	@Bean
	public GroupedOpenApi storeApi() {
		return GroupedOpenApi.builder()
			.group("storeApi")
			.pathsToMatch("/stores/**")
			.build();
	}

	@Bean
	public GroupedOpenApi themeApi() {
		return GroupedOpenApi.builder()
			.group("themeApi")
			.pathsToMatch("/themes/**")
			.build();
	}

	@Bean
	public GroupedOpenApi adminApi() {
		return GroupedOpenApi.builder()
			.group("adminApi")
			.pathsToMatch("/admin/**")
			.build();
	}
}
