package net.cproduction.oauthsystem.web;

public record TokenValidationResponse(boolean valid, boolean expired) {
}
