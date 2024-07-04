package net.cproduction.oauthsystem.web;

public record TokenRefreshResponse(String accessToken, String refreshToken) {
}
