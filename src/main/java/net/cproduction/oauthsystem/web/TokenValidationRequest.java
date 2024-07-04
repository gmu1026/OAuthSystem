package net.cproduction.oauthsystem.web;

import jakarta.validation.constraints.NotBlank;

public record TokenValidationRequest(@NotBlank(message = "Token must not be blank") String token) {
}
