package net.cproduction.oauthsystem.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.cproduction.oauthsystem.token.RefreshTokenService;
import net.cproduction.oauthsystem.token.TokenPair;
import net.cproduction.oauthsystem.token.TokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/v1")
public class TokenController {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validateToken(
            @RequestBody @Valid TokenValidationRequest request) {
        tokenProvider.validateToken(request.token());
        TokenValidationResponse response = new TokenValidationResponse(true, false);

        return ResponseEntity.ok(new ApiResponse<>(true, "Token validated success", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenPair>> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        TokenPair tokenPair = tokenProvider.refreshAccessToken(request.refreshToken());

        return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed successfully", tokenPair));
    }
}
