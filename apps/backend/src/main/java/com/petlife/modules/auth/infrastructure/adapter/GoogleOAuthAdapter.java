package com.petlife.modules.auth.infrastructure.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petlife.modules.auth.application.port.OAuthProviderPort;
import com.petlife.shared.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleOAuthAdapter implements OAuthProviderPort {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GoogleOAuthAdapter(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public GoogleUserInfo getGoogleUserInfo(String idToken) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode json = objectMapper.readTree(response);

            if (json.has("error")) {
                throw BusinessException.unauthorized("INVALID_GOOGLE_TOKEN", "O token do Google fornecido é inválido ou expirou.");
            }

            String email = json.get("email").asText();
            String name = json.get("name").asText();
            String avatarUrl = json.has("picture") ? json.get("picture").asText() : null;

            return new GoogleUserInfo(email, name, avatarUrl);
        } catch (Exception e) {
            throw BusinessException.unauthorized("INVALID_GOOGLE_TOKEN", "Falha ao validar o token com o Google: " + e.getMessage());
        }
    }
}
