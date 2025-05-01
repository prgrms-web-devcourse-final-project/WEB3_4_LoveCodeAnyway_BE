package com.ddobang.backend.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String nickname) {
		return memberRepository.findByNickname(nickname)
			.map(member -> new CustomUserDetails(member.getId(), member.getNickname(), false))
			.orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
	}
}