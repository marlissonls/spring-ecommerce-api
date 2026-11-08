package br.com.springecommerce.iam.authentication.application.dto;

public record TokenDto(
    String accessToken,
    String refreshToken
) {}
