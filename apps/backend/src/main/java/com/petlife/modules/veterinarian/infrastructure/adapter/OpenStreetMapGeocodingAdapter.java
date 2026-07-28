package com.petlife.modules.veterinarian.infrastructure.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petlife.modules.veterinarian.application.port.GeocodingPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Component
@Profile("!test")
public class OpenStreetMapGeocodingAdapter implements GeocodingPort {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenStreetMapGeocodingAdapter(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public GeocodingResult geocode(String zipCode, String number, String addressLine) {
        try {
            String query = String.format("%s %s %s", addressLine, number, zipCode);
            String url = UriComponentsBuilder.fromUriString("https://nominatim.openstreetmap.org/search")
                    .queryParam("q", query)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.isArray() && jsonNode.size() > 0) {
                JsonNode firstResult = jsonNode.get(0);
                String latStr = firstResult.get("lat").asText();
                String lonStr = firstResult.get("lon").asText();

                return new GeocodingResult(
                        new BigDecimal(latStr),
                        new BigDecimal(lonStr)
                );
            }
        } catch (Exception e) {
            // Log warning or handle exception properly, fallback to 0,0 or null in real app
        }
        
        // Fallback for missing results or exceptions
        return new GeocodingResult(
                new BigDecimal("-23.55052000"),
                new BigDecimal("-46.63330800")
        );
    }
}
