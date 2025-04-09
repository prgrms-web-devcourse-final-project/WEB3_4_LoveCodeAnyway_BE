package com.ddobang.backend.domain.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.member.entity.MemberTag;
import com.ddobang.backend.domain.member.repository.MemberTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberTagService {

	private final MemberTagRepository memberTagRepository;

	public List<MemberTag> findAllByIds(List<Long> ids) {
		return memberTagRepository.findAllById(ids);
	}
}
