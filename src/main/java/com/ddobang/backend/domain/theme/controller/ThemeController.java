package com.ddobang.backend.domain.theme.controller;

import com.ddobang.backend.domain.theme.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/themes")
public class ThemeController {
    private final ThemeService themeService;
}
