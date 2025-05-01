package com.ddobang.backend.global.security;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.global.exception.jwt.JwtErrorCode;
import com.ddobang.backend.global.exception.jwt.JwtException;

/**
 * 로그인한 사용자의 정보를 제공하는 LoginMemberProvider의 테스트 클래스입니다.
 * @author Jay Lim
 */
class LoginMemberProviderTest {

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private LoginMemberProvider loginMemberProvider;

	private AutoCloseable closeable; // MockitoAnnotations.openMocks(this)로 생성된 Mock 객체를 닫기 위한 AutoCloseable

	private static final Long MEMBER_ID = 59L;
	private static final String NICKNAME = "또방이";

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("로그인된 사용자가 존재하면 해당 Member를 반환한다")
	void getCurrentMember_success() {
		// given: 로그인한 Member 생성
		CustomUserDetails userDetails = mockUserDetails(MEMBER_ID, NICKNAME);

		// SecurityContext에 로그인 Member 설정
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
		);

		// MemberService가 반환할 Member 설정 (getCurrentMember()에서 호출됨)
		Member mockMember = new Member(MEMBER_ID, NICKNAME);
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(mockMember));

		// when: 실제 메서드 호출
		Member currentMember = loginMemberProvider.getCurrentMember();

		// then: 반환된 Member 검증
		assertThat(currentMember.getId()).isEqualTo(MEMBER_ID);
		assertThat(currentMember.getNickname()).isEqualTo(NICKNAME);
	}

	@Test
	@DisplayName("SecurityContext에 인증 정보가 없으면 예외를 발생시킨다")
	void getCurrentMember_unauthenticated() {
		// given
		SecurityContextHolder.clearContext();

		// when & then
		assertThatThrownBy(() -> loginMemberProvider.getCurrentMember())
			.isInstanceOf(JwtException.class)
			.hasMessageContaining(JwtErrorCode.INVALID_PRINCIPAL.getMessage());
	}

	private CustomUserDetails mockUserDetails(Long id, String nickname) {
		return new CustomUserDetails(id, nickname, false);
	}
}
