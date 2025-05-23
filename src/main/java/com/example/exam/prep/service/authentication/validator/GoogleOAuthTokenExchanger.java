package com.example.exam.prep.service.authentication.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class GoogleOAuthTokenExchanger implements OAuthTokenExchanger {
    @Value("${google.oauth.client.id}")
    private String CLIENT_ID;

    @Value("${google.oauth.client.secret}")
    private String CLIENT_SECRET;

    @Value("${google.oauth.redirect.uri}")
    private String REDIRECT_URI;

    @Value("${google.oauth.token.uri}")
    private String URI;

    public String exchangeCodeAndGetEmail(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);
        String response = restTemplate.postForObject(URI, requestEntity, String.class);

        // Parse the response to extract the email
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(response);
            String idToken = jsonNode.get("id_token").asText();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (idToken != null) {
                GoogleIdToken.Payload payload =  googleIdToken.getPayload();
                return payload.getEmail();
            }
        } catch (Exception e) {
            Logger.getLogger(GoogleOAuthTokenExchanger.class.getName()).log(Level.SEVERE, "An error occurred", e);
        }

        return null;
    }
}