package br.com.springecommerce.config.security.web;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public final class PublicEndpoints {

    private static final RequestMatcher MATCHER = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/v1/auth/signin"),
            new AntPathRequestMatcher("/api/v1/auth/refresh/**"),

            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/swagger-ui/**"),

            new AntPathRequestMatcher("/api/v1/users", HttpMethod.POST.name()),

            new AntPathRequestMatcher("/api/v1/products", HttpMethod.GET.name()),
            new AntPathRequestMatcher("/api/v1/products/slug/**", HttpMethod.GET.name()),

            new AntPathRequestMatcher("/api/v1/categories", HttpMethod.GET.name()),
            new AntPathRequestMatcher("/api/v1/categories/slug/**", HttpMethod.GET.name()),

            new AntPathRequestMatcher("/api/v1/brands", HttpMethod.GET.name()),
            new AntPathRequestMatcher("/api/v1/brands/slug/**", HttpMethod.GET.name())
    );

    private PublicEndpoints() {}

    public static RequestMatcher matcher() {
        return MATCHER;
    }
}