package com.ddobang.backend.domain.party.controller;

import com.ddobang.backend.domain.party.service.PartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parties")
public class PartyController {
    private final PartyService partyService;
}
