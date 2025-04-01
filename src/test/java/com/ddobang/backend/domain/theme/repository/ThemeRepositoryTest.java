package com.ddobang.backend.domain.theme.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.store.repository.StoreRepository;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeTag;
import com.ddobang.backend.global.config.QuerydslConfig;

/**
 * ThemeRepositoryTest
 * 테마에 관련된 모든 repository에 대한 테스트 코드 입니다.
 * @author 100minha
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
@Transactional
public class ThemeRepositoryTest {
	@Autowired
	private ThemeRepository themeRepository;
	@Autowired
	private ThemeTagRepository themeTagRepository;
	@Autowired
	private StoreRepository storeRepository;

	private Store store = Store.builder()
		.name("매장1")
		.address("서울시 마포구")
		.phoneNumber("1234-1234")
		.status(Store.Status.OPENED)
		.build();

	private ThemeTag tag1 = new ThemeTag("태그1");
	private ThemeTag tag2 = new ThemeTag("태그2");

	private List<Theme> testThemes = new ArrayList<>();

	@BeforeEach
	void setUp() {
		tag1 = themeTagRepository.save(tag1);
		tag2 = themeTagRepository.save(tag2);
		store = storeRepository.save(store);

		for (int i = 1; i <= 5; i++) {
			testThemes.add(themeRepository.save(Theme.builder()
				.name("방탈출" + i)
				.description("방탈출설명" + i)
				.officialDifficulty(4.0f)
				.minParticipants(1)
				.maxParticipants(8)
				.status(Theme.Status.OPENED)
				.store(store)
				.themeTags(List.of(tag1, tag2))
				.thumbnailUrl("test.thumbnail")
				.build()));
		}
	}

	@Test
	void findByIdTest() {
		// given
		Long id = 1L;

		// when
		Optional<Theme> oTheme = themeRepository.findById(id);

		// then
		assertThat(oTheme.isPresent()).isTrue();
		Theme theme = oTheme.get();
		assertThat(theme).isEqualTo(testThemes.getFirst());
	}

}
