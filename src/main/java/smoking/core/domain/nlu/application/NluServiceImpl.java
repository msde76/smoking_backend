package smoking.core.domain.nlu.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import smoking.core.domain.nlu.dto.NluResponseDTO;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NluServiceImpl implements NluService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.maps.api-key}")
    private String googleApiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent?key=";

    @Override
    public NluResponseDTO.ParseDto parseCommand(String commandText) {

        String apiUrl = GEMINI_API_URL + googleApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Gemini가 한국어 입력을 더 잘 이해하도록 시스템 프롬프트에
        // 구체적인 규칙과 "한국어 예시(Few-shot)"를 추가합니다.
        String systemPromptText = "당신은 시각장애인용 길안내 앱의 NLU(자연어 이해) 파서입니다. " +
                "사용자의 입력은 한국어입니다. " +
                "사용자의 텍스트를 분석하여, 제공된 JSON 스키마에 따라 '의도(intent)'와 '엔티티(entity)'를 정확히 추출해야 합니다. " +
                "당신은 반드시 JSON 객체로만 응답해야 합니다." +
                "\n" +
                "--- 예시 (Example) ---" +
                "\n" +
                "예시 1 (길찾기):\n" +
                "입력: \"강남역으로 길 알려줘\"\n" +
                "JSON: {\"intent\": \"SEARCH_ROUTE\", \"destination\": \"강남역\"}\n" +
                "\n" +
                "예시 2 (길찾기):\n" +
                "입력: \"서울시청 가고 싶어\"\n" +
                "JSON: {\"intent\": \"SEARCH_ROUTE\", \"destination\": \"서울시청\"}\n" +
                "\n" +
                "예시 3 (신고):\n" +
                "입력: \"여기서 사람들이 담배를 너무 많이 피워요\"\n" +
                "JSON: {\"intent\": \"REPORT_SMOKING\", \"reportContent\": \"여기서 사람들이 담배를 너무 많이 피워요\"}\n" +
                "\n" +
                "예시 4 (알 수 없음):\n" +
                "입력: \"오늘 날씨 어때?\"\n" +
                "JSON: {\"intent\": \"UNKNOWN\"}\n" +
                "--- (예시 끝) ---" +
                "\n" +
                "이제 다음 입력을 분석하세요.";

        Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(Map.of("text", systemPromptText))
        );

        Map<String, Object> contents = Map.of(
                "parts", List.of(Map.of("text", commandText))
        );

        Map<String, Object> jsonSchema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "intent", Map.of(
                                "type", "STRING",
                                "description", "Recognized user intent.",
                                "enum", List.of("SEARCH_ROUTE", "REPORT_SMOKING", "UNKNOWN")
                        ),
                        "destination", Map.of(
                                "type", "STRING",
                                "description", "The destination address or landmark, if intent is SEARCH_ROUTE."
                        ),
                        "reportContent", Map.of( // [!] 'reportContent' (camelCase)
                                "type", "STRING",
                                "description", "The content of the user's report, if intent is REPORT_SMOKING."
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
            JsonNode responseNode = restTemplate.postForObject(apiUrl, entity, JsonNode.class);

            if (responseNode == null || !responseNode.has("candidates")) {
                throw new RuntimeException("Gemini API 응답이 비어있거나 'candidates' 필드가 없습니다.");
            }

            String jsonText = responseNode
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return objectMapper.readValue(jsonText, NluResponseDTO.ParseDto.class);

        } catch (Exception e) {
            e.printStackTrace();
            // 실패 시 기본 UNKNOWN DTO 반환
            return NluResponseDTO.ParseDto.builder().intent("UNKNOWN").build();
        }
    }
}