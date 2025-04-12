package com.ddobang.backend.domain.member.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.member.dto.response.MemberStatResponse;
import com.ddobang.backend.domain.member.dto.response.OtherProfileResponse;
import com.ddobang.backend.domain.member.entity.EscapeProfileStat;
import com.ddobang.backend.domain.member.entity.EscapeScheduleStat;
import com.ddobang.backend.domain.member.entity.EscapeSummaryStat;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.entity.MemberStat;
import com.ddobang.backend.domain.member.entity.MemberTag;
import com.ddobang.backend.domain.member.entity.MemberTagMapping;
import com.ddobang.backend.domain.member.exception.MemberErrorCode;
import com.ddobang.backend.domain.member.exception.MemberException;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.member.repository.MemberStatRepository;
import com.ddobang.backend.domain.member.repository.MemberTagMappingRepository;
import com.ddobang.backend.global.auth.dto.request.SignupRequest;
import com.ddobang.backend.global.exception.auth.AuthErrorCode;
import com.ddobang.backend.global.exception.auth.AuthException;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final MemberTagMappingRepository memberTagMappingRepository;
	private final MemberTagService memberTagService;
	private final MemberStatRepository memberStatRepository;

	// OAuth2User 정보로 회원 생성
	public Member createMemberFromOAuth2(OAuth2User oAuth2User) {
		String kakaoId = oAuth2User.getAttribute("id").toString();

		Map<String, Object> properties = oAuth2User.getAttribute("properties");
		String nickname = null;

		if (properties != null && properties.containsKey("nickname")) {
			nickname = (String)properties.get("nickname");
		}

		// 닉네임 없으면 기본값 사용
		if (nickname == null || nickname.isBlank()) {
			nickname = "기본닉네임";
		}

		Member member = Member.builder()
			.kakaoId(kakaoId)
			.nickname(nickname)
			.build();

		return memberRepository.save(member);
	}

	// 회원 ID로 회원 조회
	public Member getMemberById(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
	}

	// 다른 회원의 프로필 조회
	@Transactional(readOnly = true)
	public OtherProfileResponse getOtherProfile(Long id) {
		Member member = getMemberById(id);
		return OtherProfileResponse.of(member);
	}

	public Member getByKakaoId(String kakaoId) {
		return memberRepository.findByKakaoId(kakaoId);
	}

	public boolean existsByKakaoId(String kakaoId) {
		return memberRepository.existsByKakaoId(kakaoId);
	}

	public boolean existsByNickname(@NotBlank(message = "닉네임은 필수입니다.") String nickname) {
		return memberRepository.existsByNickname(nickname);
	}

	// 회원ID로 회원 조회
	public Member getById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
	}

	// 닉네임으로 회원 조회
	public Member getByNickname(String nickname) {
		return memberRepository.findByNickname(nickname)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
	}

	// 닉네임으로 회원 조회
	public Member getMemberByUsername(String username) {
		return memberRepository.findByNickname(username)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
	}

	// 회원 정보 저장
	public Member save(Member member) {
		return memberRepository.save(member);
	}

	public Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
	}

	// 회원가입시 회원 등록, 중복 검사 및 태그 매핑
	@Transactional
	public Member registerMember(String kakaoId, SignupRequest request) {
		validateDuplicateKakaoId(kakaoId);
		validateDuplicateNickname(request.nickname());

		Member member = request.toEntity(kakaoId);
		save(member);

		assignTags(member, request.tags());
		return member;
	}

	public MemberStatResponse getMemberStat(Member member) {
		Optional<MemberStat> memberStat = memberStatRepository.findById(member.getId());

		if (memberStat.isEmpty()) {
			return null;
		}

		EscapeSummaryStat escapeSummaryStat = memberStat.get().getEscapeSummaryStat();
		EscapeProfileStat escapeProfileStat = memberStat.get().getEscapeProfileStat();
		EscapeScheduleStat escapeScheduleStat = memberStat.get().getEscapeScheduleStat();

		return MemberStatResponse.of(escapeSummaryStat, escapeProfileStat, escapeScheduleStat);
	}

	private void validateDuplicateKakaoId(String kakaoId) {
		if (existsByKakaoId(kakaoId)) {
			throw new AuthException(AuthErrorCode.ALREADY_REGISTERED);
		}
	}

	private void validateDuplicateNickname(String nickname) {
		if (existsByNickname(nickname)) {
			throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
		}
	}

	// 회원 태그 매핑
	private void assignTags(Member member, List<Long> selectTagIds) {
		List<MemberTag> tags = memberTagService.findAllByIds(selectTagIds);
		for (MemberTag tag : tags) {
			MemberTagMapping mapping = new MemberTagMapping(member, tag);
			memberTagMappingRepository.save(mapping);
		}
	}
}
