package com.ddobang.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    // Store
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_001", "매장을 찾을 수 없습니다."),
    STORE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "STORE_002", "이미 존재하는 매장입니다."),

    // Theme
    THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "THEME_001", "테마를 찾을 수 없습니다."),

    // Theme tag
    THEME_TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "THEME_TAG_001", "테마 태그를 찾을 수 없습니다."),

    // Party
    PARTY_NOT_FOUND(HttpStatus.NOT_FOUND, "PARTY_001", "모임을 찾을 수 없습니다."),
    PARTY_FULL(HttpStatus.BAD_REQUEST, "PARTY_002", "모임 인원이 가득 찼습니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_001", "멤버를 찾을 수 없습니다."),
    MEMBER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "MEMBER_002", "멤버 인증에 실패했습니다."),

    // Member Tag
    MEMBER_TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_TAG_001", "멤버 태그를 찾을 수 없습니다."),

    // Member Review
    MEMBER_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_001", "멤버 리뷰를 찾을 수 없습니다."),
    MEMBER_REVIEW_DUPLICATED(HttpStatus.BAD_REQUEST, "REVIEW_002", "이미 멤버 리뷰를 작성하셨습니다."),

    // Diary
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY_001", "다이어리를 찾을 수 없습니다."),

    // Board
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD_001", "게시글을 찾을 수 없습니다."),
    BOARD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "BOARD_002", "게시글에 접근할 수 없습니다."),

    // Message
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE_001", "쪽지를 찾을 수 없습니다."),
    MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MESSAGE_002", "쪽지 전송에 실패했습니다."),

    // Notification
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI_001", "알림을 찾을 수 없습니다."),

    // Common
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_002", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_003", "접근 권한이 없습니다."),

    // Server
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_500", "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
