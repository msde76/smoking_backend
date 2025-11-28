package smoking.core.domain.nlu.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import smoking.core.domain.nlu.dto.NluResponseDTO;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NluServiceImpl implements NluService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.maps.api-key}")
    private String googleApiKey;

    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent";

    @Override
    public NluResponseDTO.ParseDto parseCommand(String commandText) {

        log.info(">>> [NLU Request] 입력된 명령어: {}", commandText);

        URI uri = UriComponentsBuilder.fromHttpUrl(GEMINI_BASE_URL)
                .queryParam("key", googleApiKey)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // --- 시스템 프롬프트 설정 (새로운 예시 추가) ---
        String systemPromptText = "당신은 시각장애인용 길안내 앱의 NLU(자연어 이해) 파서입니다. " +
                "사용자의 입력은 한국어입니다. " +
                "사용자의 텍스트를 분석하여, 제공된 JSON 스키마에 따라 '의도(intent)'와 '엔티티(entity)'를 정확히 추출해야 합니다. " +
                "당신은 반드시 JSON 객체로만 응답해야 합니다." +
                "\n--- 예시 (Example) ---\n" +
                "예시 1: \"강남역으로 길 알려줘\" -> {\"intent\": \"SEARCH_ROUTE\", \"destination\": \"강남역\"}\n" +
                "예시 2: \"담배 신고\" -> {\"intent\": \"REPORT_SMOKING\", \"reportContent\": \"...\"}\n" +
                "예시 3: \"보이스 속도 올려\" -> {\"intent\": \"VOICE_SPEED_CONTROL\", \"controlAction\": \"상승\"}\n" +
                "예시 4: \"목소리 톤 초기화\" -> {\"intent\": \"VOICE_TONE_CONTROL\", \"controlAction\": \"초기화\"}\n" +
                "예시 5: \"화면 크기 낮춰줘\" -> {\"intent\": \"UI_SIZE_CONTROL\", \"controlAction\": \"하락\"}\n" +
                "예시 6: \"오늘 날씨\" -> {\"intent\": \"UNKNOWN\"}\n" +
                "--- (예시 끝) ---\n" +
                "이제 다음 입력을 분석하세요.";

        Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(Map.of("text", systemPromptText))
        );

        Map<String, Object> contents = Map.of(
                "parts", List.of(Map.of("text", commandText))
        );

        // --- JSON 스키마 설정 (새로운 Intent와 Entity 추가) ---
        Map<String, Object> jsonSchema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "intent", Map.of(
                                "type", "STRING",
                                "enum", List.of(
                                        "SEARCH_ROUTE",
                                        "REPORT_SMOKING",
                                        "VOICE_SPEED_CONTROL", // 추가
                                        "VOICE_TONE_CONTROL", // 추가
                                        "UI_SIZE_CONTROL", // 추가
                                        "UNKNOWN"
                                )
                        ),
                        "destination", Map.of("type", "STRING"),
                        "reportContent", Map.of("type", "STRING"),
                        "controlAction", Map.of( // 추가: 제어 행동 (상승, 하락, 초기화)
                                "type", "STRING",
                                "enum", List.of("상승", "하락", "초기화")
                        )
                ),
                "required", List.of("intent")
        );

        Map<String, Object> generationConfig = Map.of(
                "responseMimeType", "application/json",
                "responseSchema", jsonSchema
        );

        Map<String, Object> payload = Map.of(
                "contents", List.of(contents),
                "systemInstruction", systemInstruction,
                "generationConfig", generationConfig
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            JsonNode responseNode = restTemplate.postForObject(uri, entity, JsonNode.class);

            if (responseNode == null || !responseNode.has("candidates")) {
                log.error(">>> [NLU Error] Gemini 응답 없음");
                throw new RuntimeException("Gemini API 응답 오류");
            }

            String jsonText = responseNode.path("candidates")
                    .get(0).path("content").path("parts").get(0).path("text").asText();

            log.info(">>> [NLU Response] 분석 결과: {}", jsonText);

            return objectMapper.readValue(jsonText, NluResponseDTO.ParseDto.class);

        } catch (Exception e) {
            log.error(">>> [NLU Exception] API 호출 중 오류: {}", e.getMessage());
            e.printStackTrace();
            return NluResponseDTO.ParseDto.builder().intent("UNKNOWN").build();
        }
    }
}