package br.com.springecommerce.iam.authentication.application.dto;

import java.util.List;
import java.util.UUID;

public record AuthenticationResponseDto(
        UUID userId,
        UUID customerId,
        String name,
        String email,
        List<String> roles,
        String accessToken,
        String refreshToken
) {}
