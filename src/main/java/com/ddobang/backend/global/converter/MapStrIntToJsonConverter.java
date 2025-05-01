package com.ddobang.backend.global.converter;

import java.io.IOException;
import java.util.Map;

import com.ddobang.backend.global.config.AppConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MapStrIntToJsonConverter implements AttributeConverter<Map<String, Integer>, String> {
	@Override
	public String convertToDatabaseColumn(Map<String, Integer> attribute) {
		try {
			return AppConfig.getObjectMapper().writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, Integer> convertToEntityAttribute(String dbData) {
		try {
			return AppConfig.getObjectMapper().readValue(dbData, new TypeReference<Map<String, Integer>>() {
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
