package br.com.springecommerce.iam.user.application.dto;

import br.com.springecommerce.customer.customer.domain.exception.CustomerErrors;
import br.com.springecommerce.iam.user.domain.exception.UserErrors;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record UserCreateDto(
        @NotBlank(message = CustomerErrors.Code.CUSTOMER_NAME_REQUIRED)
        @Size(min = 3, max = 100, message = CustomerErrors.Code.CUSTOMER_NAME_SIZE)
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ\\s]*$", message = CustomerErrors.Code.CUSTOMER_NAME_INVALID_FORMAT)
        String name,

        @NotBlank(message = CustomerErrors.Code.CUSTOMER_CPF_REQUIRED)
        @CPF(message = CustomerErrors.Code.CUSTOMER_CPF_INVALID)
        String cpf,

        @NotBlank(message = UserErrors.Code.USER_EMAIL_REQUIRED)
        @Email(message = UserErrors.Code.USER_EMAIL_INVALID)
        String email,

        @NotBlank(message = UserErrors.Code.USER_PASSWORD_REQUIRED)
        @Size(min = 8, message = UserErrors.Code.USER_PASSWORD_SIZE)
        String password
) {}
