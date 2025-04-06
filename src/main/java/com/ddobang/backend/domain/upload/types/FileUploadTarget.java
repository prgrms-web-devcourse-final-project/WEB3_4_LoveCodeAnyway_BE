package com.ddobang.backend.domain.upload.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileUploadTarget {
	PROFILE("profile"),
	DIARY("diary"),
	BOARD("board"),
	NONE("none");

	private final String type;
}
