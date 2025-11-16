package smoking.core.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import smoking.core.domain.smoking.domain.entity.AreaStatus;
import smoking.core.domain.smoking.domain.entity.SmokingAreaType;
import smoking.core.domain.smoking.domain.entity.SmokingArea;
import smoking.core.domain.smoking.domain.repository.SmokingAreaRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final SmokingAreaRepository smokingAreaRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        // 1. DB에 이미 데이터가 있는지 확인 (있으면 실행 안 함)
        if (smokingAreaRepository.count() > 0) {
            log.info("[DataInitializer] DB에 이미 데이터가 존재하여 초기화를 건너뜁니다.");
            return;
        }

        log.info("[DataInitializer] DB 초기화를 시작합니다...");

        // 2. /resources/smoking_areas_seoul.csv 파일 읽기
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("smoking_areas_seoul.csv").getInputStream(), "UTF-8"
                )
        )) {

            // 3. (중요) 첫 번째 줄(헤더) 건너뛰기
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                try {
                    // 4. CSV 데이터를 SmokingArea 엔티티로 변환
                    SmokingArea area = SmokingArea.builder()
                            .address(data[0].replace("\"", "")) // 따옴표 제거
                            .latitude(new BigDecimal(data[1]))
                            .longitude(new BigDecimal(data[2]))
                            .type(SmokingAreaType.valueOf(data[3])) // "공공부스"
                            .source(data[4])
                            .status(AreaStatus.valueOf(data[5])) // "승인"
                            .build();

                    // 5. DB에 저장
                    smokingAreaRepository.save(area);

                } catch (Exception e) {
                    log.warn("[DataInitializer] 데이터 파싱 오류 (한 줄 건너뜀): {}", line);
                }
            }
            log.info("[DataInitializer] DB 초기화 완료.");

        } catch (Exception e) {
            log.error("[DataInitializer] CSV 파일 로드 실패", e);
        }
    }
}
