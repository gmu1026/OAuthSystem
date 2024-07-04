package net.cproduction.oauthsystem.web;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank(message = "Refresh token must not be blank") String refreshToken) {
}
