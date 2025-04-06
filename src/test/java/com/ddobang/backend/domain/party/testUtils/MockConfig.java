package com.ddobang.backend.domain.party.testUtils;

import com.ddobang.backend.domain.party.PartyAuthHelper;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

// TODO: 삭제할 예정인 임시 인증 클래스입니다.
@TestConfiguration
@Profile("test")
public class MockConfig {
    @Bean
    public PartyAuthHelper partyAuthHelper() {
        return Mockito.mock(PartyAuthHelper.class);
    }
}
