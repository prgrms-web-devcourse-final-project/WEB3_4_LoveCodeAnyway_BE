package com.ddobang.backend.domain.member.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.member.dto.request.UpdateProfileRequest;
import com.ddobang.backend.domain.member.dto.response.BasicProfileResponse;
import com.ddobang.backend.domain.member.dto.response.MemberStatResponse;
import com.ddobang.backend.domain.member.dto.response.MemberTagResponse;
import com.ddobang.backend.domain.member.dto.response.OtherProfileResponse;
import com.ddobang.backend.domain.member.dto.stat.EscapeProfileSummaryDto;
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
import com.ddobang.backend.domain.member.repository.MemberTagRepository;
import com.ddobang.backend.global.auth.dto.request.SignupRequest;
import com.ddobang.backend.global.exception.auth.AuthErrorCode;
import com.ddobang.backend.global.exception.auth.AuthException;
import com.ddobang.backend.global.security.LoginMemberProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final MemberTagMappingRepository memberTagMappingRepository;
	private final MemberTagService memberTagService;
	private final MemberStatRepository memberStatRepository;
	private final LoginMemberProvider loginMemberProvider;
	private final MemberTagRepository memberTagRepository;

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

	@Transactional(readOnly = true)
	public Member getByKakaoId(String kakaoId) {
		return memberRepository.findByKakaoId(kakaoId);
	}

	@Transactional(readOnly = true)
	public boolean existsByKakaoId(String kakaoId) {
		return memberRepository.existsByKakaoId(kakaoId);
	}

	@Transactional(readOnly = true)
	public boolean existsByNickname(String nickname) {
		return memberRepository.existsByNickname(nickname);
	}

	// 회원ID로 회원 조회
	@Transactional(readOnly = true)
	public Member getById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
	}

	// 닉네임으로 회원 조회
	@Transactional(readOnly = true)
	public Member getByNickname(String nickname) {
		return memberRepository.findByNickname(nickname)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
	}

	// 닉네임으로 회원 조회
	@Transactional(readOnly = true)
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

	// 내 프로필 수정
	@Transactional
	public BasicProfileResponse updateProfile(Long memberId, UpdateProfileRequest request) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

		member.updateProfile(
			request.nickname(),
			request.introduction(),
			request.profileImageUrl()
		);

		return BasicProfileResponse.of(member);
	}

	// 내 사용자 태그 조회
	@Transactional(readOnly = true)
	public List<MemberTagResponse> getMyTags() {
		Member currentMember = loginMemberProvider.getCurrentMember();

		List<MemberTagMapping> mappings = memberTagMappingRepository.findByMemberId(currentMember.getId());

		return mappings.stream()
			.map(mapping -> MemberTagResponse.from(mapping.getTag()))
			.toList();
	}

	// 내 사용자 태그 수정
	@Transactional
	public void updateTags(Long memberId, List<Long> tagIds) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

		// 기존 태그 매핑 삭제
		memberTagMappingRepository.deleteByMemberId(memberId);

		// 새 태그 리스트 조회
		List<MemberTag> tags = memberTagRepository.findAllById(tagIds);

		// 새 매핑 등록
		List<MemberTagMapping> mappings = tags.stream()
			.map(tag -> new MemberTagMapping(member, tag))
			.toList();

		memberTagMappingRepository.saveAll(mappings);
	}

	// 사용자(나, 타인) 통계 조회
	@Transactional(readOnly = true)
	public EscapeProfileSummaryDto getStatsByMemberId(Long memberId) {
		MemberStat stat = memberStatRepository.findByMemberId(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_STAT));
		return EscapeProfileSummaryDto.from(stat.getEscapeSummaryStat());
	}

	// 타 회원 프로필 조회
	@Transactional(readOnly = true)
	public OtherProfileResponse getOtherProfile(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

		return OtherProfileResponse.of(member);
	}

	// 타 회원 사용자 태그 조회
	@Transactional(readOnly = true)
	public List<MemberTagResponse> getTagsByMemberId(Long memberId) {
		List<MemberTagMapping> mappings = memberTagMappingRepository.findByMemberId(memberId);
		return mappings.stream()
			.map(mapping -> MemberTagResponse.from(mapping.getTag()))
			.toList();
	}
}
