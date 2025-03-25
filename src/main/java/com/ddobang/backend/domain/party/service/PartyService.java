package com.ddobang.backend.domain.party.service;

import com.ddobang.backend.domain.party.repository.PartyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyService {
    private final PartyRepository partyRepository;
}
