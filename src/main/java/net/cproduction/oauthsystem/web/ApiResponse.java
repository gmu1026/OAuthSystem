package net.cproduction.oauthsystem.web;

public record ApiResponse<T>(boolean success, String message, T data) {
}
