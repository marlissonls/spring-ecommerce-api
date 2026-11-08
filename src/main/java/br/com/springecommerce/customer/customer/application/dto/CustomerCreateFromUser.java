package br.com.springecommerce.customer.customer.application.dto;

public record CustomerCreateFromUser(
        String name,
        String cpf,
        String birthDate,
        String phone
) {}
