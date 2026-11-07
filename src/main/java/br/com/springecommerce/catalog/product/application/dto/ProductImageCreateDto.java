package br.com.springecommerce.catalog.product.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.URL;

public record ProductImageCreateDto(
        @NotBlank(message = "O publicId da imagem é obrigatório")
        String publicId,

        @NotBlank(message = "A URL da imagem é obrigatória")
        @URL(message = "A URL informada deve ser válida")
        String url,

        @NotNull(message = "A posição da imagem é obrigatória")
        @PositiveOrZero(message = "A posição deve ser zero ou um número positivo")
        Integer position,

        @NotNull(message = "O campo indicando se é a imagem principal é obrigatório")
        Boolean main
) {}

