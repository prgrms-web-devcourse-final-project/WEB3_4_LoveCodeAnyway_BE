package com.ddobang.backend.domain.upload.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileUploadTarget {
	PROFILE("profile"),
	DIARY("diary"),
	POST("post"),
	NONE("none");

	private final String type;
}
