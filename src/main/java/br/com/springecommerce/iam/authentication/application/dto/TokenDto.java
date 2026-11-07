package br.com.springecommerce.iam.authentication.application.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record TokenDto(
    UUID id,
    String email,
    List<String> roles,
    Boolean authenticated,
    Date created,
    Date expiration,
    String accessToken,
    String refreshToken
) {}
