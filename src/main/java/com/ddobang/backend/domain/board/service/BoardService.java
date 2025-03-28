package com.ddobang.backend.domain.board.service;

import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.board.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	private final BoardRepository boardRepository;
}
