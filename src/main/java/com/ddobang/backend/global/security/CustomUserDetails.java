package com.ddobang.backend.global.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record CustomUserDetails(Long memberId, String nickname, boolean isAdmin) implements UserDetails {

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (isAdmin) {
			return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
		} else {
			return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
		}
	}

	@Override
	public String getPassword() {
		return null; // 비밀번호 인증 안 쓰니까 null
	}

	@Override
	public String getUsername() {
		return nickname;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
