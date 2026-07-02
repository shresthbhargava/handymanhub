package com.handymanhub.integration;

import com.handymanhub.AbstractIntegrationTest;
import com.handymanhub.dto.response.AuthResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuthIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Map<String, String> registerBody(String email, String password) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("name", "Test User");
        body.put("role", "CUSTOMER");
        return body;
    }

    @Test
    void register_createsUserAndReturnsTokens() {
        ResponseEntity<AuthResponseDto> response = restTemplate.postForEntity(
                "/api/v1/auth/register", registerBody("testuser@example.com", "StrongPass123!"),
                AuthResponseDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
        assertNotNull(response.getBody().getRefreshToken());
        assertNotNull(response.getBody().getUserId());
    }

    @Test
    void login_returnsTokensForExistingUser() {
        restTemplate.postForEntity("/api/v1/auth/register",
                registerBody("loginuser@example.com", "StrongPass123!"), AuthResponseDto.class);

        ResponseEntity<AuthResponseDto> response = restTemplate.postForEntity(
                "/api/v1/auth/login", registerBody("loginuser@example.com", "StrongPass123!"),
                AuthResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    void register_duplicateEmail_returns400() {
        restTemplate.postForEntity("/api/v1/auth/register",
                registerBody("dup@example.com", "StrongPass123!"), AuthResponseDto.class);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/auth/register", registerBody("dup@example.com", "StrongPass123!"),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void refreshToken_rotation_works() {
        ResponseEntity<AuthResponseDto> reg = restTemplate.postForEntity(
                "/api/v1/auth/register", registerBody("refresh@example.com", "StrongPass123!"),
                AuthResponseDto.class);

        String originalRefreshToken = reg.getBody().getRefreshToken();

        Map<String, String> refreshBody = new LinkedHashMap<>();
        refreshBody.put("refreshToken", originalRefreshToken);

        ResponseEntity<AuthResponseDto> refreshResponse = restTemplate.postForEntity(
                "/api/v1/auth/refresh", refreshBody, AuthResponseDto.class);

        assertEquals(HttpStatus.OK, refreshResponse.getStatusCode());
        assertNotNull(refreshResponse.getBody().getToken());

        String newRefreshToken = refreshResponse.getBody().getRefreshToken();
        assertNotEquals(originalRefreshToken, newRefreshToken, "Token should rotate");
    }

    @Test
    void refreshToken_reuse_isRejected() {
        ResponseEntity<AuthResponseDto> reg = restTemplate.postForEntity(
                "/api/v1/auth/register", registerBody("reuse@example.com", "StrongPass123!"),
                AuthResponseDto.class);

        String originalRefreshToken = reg.getBody().getRefreshToken();

        Map<String, String> refreshBody = new LinkedHashMap<>();
        refreshBody.put("refreshToken", originalRefreshToken);
        restTemplate.postForEntity("/api/v1/auth/refresh", refreshBody, AuthResponseDto.class);

        ResponseEntity<String> reuseResponse = restTemplate.postForEntity(
                "/api/v1/auth/refresh", refreshBody, String.class);

        assertTrue(reuseResponse.getStatusCode() == HttpStatus.UNAUTHORIZED
                        || reuseResponse.getStatusCode() == HttpStatus.BAD_REQUEST,
                "Reused token should be rejected");
    }
}