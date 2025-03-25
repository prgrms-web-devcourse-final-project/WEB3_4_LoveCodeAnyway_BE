package com.ddobang.backend.domain.theme.service;

import com.ddobang.backend.domain.theme.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThemeService {
    private final ThemeRepository themeRepository;
}
