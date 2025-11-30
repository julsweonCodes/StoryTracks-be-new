package com.T4.storyTracks.service;

import com.T4.storyTracks.dto.request.AIGenerateRequest;
import com.T4.storyTracks.dto.response.AiGenerateResponse;
import com.T4.storyTracks.dto.response.ImageResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GeminiService {
    @Value("${google.api-key}")
    private String apiKey;

    @Value("${google.model-name}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public AiGenerateResponse generate(AIGenerateRequest req) {

        String prompt = buildPrompt(req);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + modelName + ":generateContent?key=" + apiKey;

        // JSON body
        String requestBody = """
            {
              "contents": [
                {
                  "parts": [
                    { "text": "%s" }
                  ]
                }
              ]
            }
            """.formatted(escape(prompt));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class);

        try {
            JsonNode root = mapper.readTree(response.getBody());

            String text = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return AiGenerateResponse.builder()
                    .aiGenText(text)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage());
        }
    }

    private String escape(String input) {
        return input.replace("\"", "\\\"");
    }

    private String buildPrompt(AIGenerateRequest req) {

        StringBuilder imgInfo = new StringBuilder();

        if (req.getBlogImgList() != null) {
            for (ImageResponse img : req.getBlogImgList()) {
                imgInfo.append("""
                    - Image file: %s
                      Path: %s
                      Latitude: %s
                      Longitude: %s
                      Thumbnail: %s
                    """.formatted(
                        img.getImgFileName(),
                        img.getImgPath(),
                        img.getGeoLat(),
                        img.getGeoLong(),
                        Boolean.TRUE.equals(img.getThumbYn()) ? "YES" : "NO"
                ));
            }
        }

        return """
            Rewrite the text below into a short, stylish summary in markdown format.
            Keep the total output under **500 characters**.
            Do NOT exceed this limit.

            --- Title ---
            %s

            --- Original Text ---
            %s

            --- Provided Images ---
            %s

            --- Additional Directions ---
            %s

            --- Requirements ---
            Your summary must be:
            - expressive
            - emotional
            - consistent with the writer’s personality
            - include emojis sparingly
            - but stay concise and readable
            - Write in a cohesive story format.
            - Include geographical hints if relevant.
            - **important** write in markdown format you should include <img> tags in the right place and right order
            - **important** imagees should be in <img{filename}</img> this format, not [file_name.jpg] like this
            ex) <img>DSC01285.JPG</img>

            Return ONLY the generated blog text.
            """.formatted(
                req.getTitle(),
                req.getOgText(),
                imgInfo,
                req.getAiGuide()
        );
    }

}
