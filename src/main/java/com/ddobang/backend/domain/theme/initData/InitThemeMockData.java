// package com.ddobang.backend.domain.theme.initData;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.IntStream;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.ApplicationRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Lazy;
//
// import com.ddobang.backend.domain.region.entity.Region;
// import com.ddobang.backend.domain.region.repository.RegionRepository;
// import com.ddobang.backend.domain.store.entity.Store;
// import com.ddobang.backend.domain.store.repository.StoreRepository;
// import com.ddobang.backend.domain.theme.entity.Theme;
// import com.ddobang.backend.domain.theme.entity.ThemeStat;
// import com.ddobang.backend.domain.theme.entity.ThemeTag;
// import com.ddobang.backend.domain.theme.repository.ThemeRepository;
// import com.ddobang.backend.domain.theme.repository.ThemeStatRepository;
// import com.ddobang.backend.domain.theme.repository.ThemeTagRepository;
//
// import jakarta.transaction.Transactional;
// import lombok.Getter;
// import lombok.RequiredArgsConstructor;
//
// /**
//  * InitMockData
//  * 앱 실행 시 테마 Mock 데이터 등록
//  * @author 100minha
//  */
// @Configuration
// @RequiredArgsConstructor
// public class InitThemeMockData {
//
// 	private final RegionRepository regionRepository;
// 	private final StoreRepository storeRepository;
// 	private final ThemeRepository themeRepository;
// 	private final ThemeStatRepository themeStatRepository;
// 	private final ThemeTagRepository themeTagRepository;
//
// 	@Autowired
// 	@Lazy
// 	private InitThemeMockData self;
//
// 	@Getter
// 	private Region region1;
// 	@Getter
// 	private Region region2;
//
// 	@Getter
// 	private Store store1;
// 	@Getter
// 	private Store store2;
// 	@Getter
// 	private ThemeTag tag1;
// 	@Getter
// 	private ThemeTag tag2;
// 	@Getter
// 	private ThemeTag tag3;
//
// 	@Getter
// 	private List<Theme> themes = new ArrayList<>();
// 	@Getter
// 	private List<ThemeStat> themeStats = new ArrayList<>();
//
// 	@Bean
// 	public ApplicationRunner themeInitDataApplicationRunner() {
// 		return args -> {
// 			self.initMockData();
// 		};
// 	}
//
// 	@Transactional
// 	public void initMockData() {
// 		if (themeRepository.count() > 0)
// 			return;
//
// 		// 1. 지역 2개 저장
// 		region1 = regionRepository.save(new Region("서울", "강남"));
// 		region2 = regionRepository.save(new Region("서울", "홍대"));
//
// 		// 2. 매장 2개 저장
// 		store1 = storeRepository.save(Store.builder()
// 			.name("방탈출 A")
// 			.address("서울 강남구")
// 			.phoneNumber("010-1111-1111")
// 			.status(Store.Status.OPENED)
// 			.region(region1)
// 			.build());
//
// 		store2 = storeRepository.save(Store.builder()
// 			.name("방탈출 B")
// 			.address("서울 마포구")
// 			.phoneNumber("010-2222-2222")
// 			.status(Store.Status.OPENED)
// 			.region(region2)
// 			.build());
//
// 		// 3. 태그 2개 저장
// 		tag1 = themeTagRepository.save(new ThemeTag("공포"));
// 		tag2 = themeTagRepository.save(new ThemeTag("감성"));
// 		tag3 = themeTagRepository.save(new ThemeTag("판타지"));
//
// 		// 4. 테마 10개 저장
// 		themes = IntStream.range(1, 11)
// 			.mapToObj(i -> themeRepository.save(Theme.builder()
// 				.name("테마 " + i)
// 				.description("테마 설명 " + i)
// 				.officialDifficulty(3.0f)
// 				.runtime(60)
// 				.minParticipants(i % 2 == 0 ? 2 : 4)
// 				.maxParticipants(i % 2 == 0 ? 3 : 5)
// 				.price(25000)
// 				.status(i % 3 != 0 ? Theme.Status.OPENED : Theme.Status.CLOSED)
// 				.reservationUrl("https://example.com/theme/" + i)
// 				.thumbnailUrl("https://placehold.co/600x400?text=Theme" + i)
// 				.store(i % 2 == 0 ? store1 : store2)
// 				.themeTags(i % 4 != 0 ? List.of(tag1, tag2) : List.of(tag3))
// 				.build()))
// 			.toList();
//
// 		// 5. 테마 통계 5개 저장
// 		themeStats = IntStream.range(0, 5)
// 			.mapToObj(i -> themeStatRepository.save(ThemeStat.builder()
// 				.theme(themes.get(i))
// 				.difficulty(3)
// 				.fear(2)
// 				.activity(4)
// 				.satisfaction(5)
// 				.production(3)
// 				.story(4)
// 				.question(3)
// 				.interior(4)
// 				.deviceRatio(75)
// 				.noHintEscapeRate(80)
// 				.escapeResult(60)
// 				.escapeTimeAvg(3600)
// 				.build()))
// 			.toList();
// 	}
// }
