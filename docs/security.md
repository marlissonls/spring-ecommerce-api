# Security

## 1. Overview

The application uses Spring Security to authenticate users, validate JWTs, and enforce authorization rules. Security configuration is centralized in `config/security`, while IAM-specific authentication components reside in `iam/security`.

The implementation separates credential authentication, token issuance, request authentication, and authorization.

## 2. Authentication

### Credential Authentication

Users authenticate through `POST /api/v1/auth/signin`, submitting their email and password.

The authentication flow delegates credential verification to Spring Security's `AuthenticationManager`. The `AuthenticatedUserDetailsService` loads the user by email, retrieves their roles, and provides the account information required for authentication.

Password verification uses a `DelegatingPasswordEncoder` configured with PBKDF2 and HMAC-SHA256.

### JWT Authentication

After successful authentication, the application issues an access token and a refresh token.

Tokens are signed using HMAC-SHA256. The token provider distinguishes access tokens from refresh tokens through the `type` claim and uses separate verifiers for each token type.

Tokens contain the user's identifier, email, roles, issue time, expiration time, and issuer where applicable. Refresh tokens also contain a unique `jti` claim.

The access token is supplied through the HTTP `Authorization` header. `RequestAuthenticationFilter` extracts the token, validates it, and establishes the authenticated principal in Spring Security's `SecurityContext`.

Invalid access tokens are converted into application exceptions and delegated to the configured exception-resolution mechanism.

### Token Refresh

Clients can submit a refresh token to `POST /api/v1/auth/refresh` to request new tokens. Refresh tokens are validated separately from access tokens.

## 3. Authorization

Authorization is enforced at both the HTTP request level and the method level.

### Request-Level Authorization

`SecurityConfig` defines which endpoint groups are public and which require authentication:

- Endpoints classified as public are accessible without authentication.
- Endpoints classified as protected require an authenticated principal.
- Other routes are permitted through the HTTP authorization rules so that unknown routes can be handled by the MVC layer.

The final rule does not make every application operation public: method-level authorization and application-specific checks may still apply. However, any sensitive endpoint not covered by the protected endpoint matchers or additional authorization rules requires careful review.

### Method-Level Authorization

Method-level security is enabled through `@EnableMethodSecurity`. Controllers and other secured components can use annotations such as `@PreAuthorize` to enforce role-based or resource-specific access rules.

For example, a method can require the `CUSTOMER` role or restrict deletion to administrators and the user whose identifier matches the target resource.

Authorization decisions should be enforced server-side and must not rely solely on client-provided identifiers.

## 4. HTTP Security and CORS

The application disables HTTP Basic authentication and CSRF protection, and configures stateless session management.

The stateless configuration means the application does not rely on an HTTP session to retain the authenticated user's security context across requests. Each authenticated request supplies its access token.

CSRF protection is disabled on the assumption that authentication is based on tokens supplied explicitly in the request rather than automatically attached browser credentials such as session cookies. This assumption should be revisited if the authentication mechanism changes.

CORS is configured through the `cors.originPatterns` application property. The configuration allows the `GET`, `POST`, `PUT`, `DELETE`, and `OPTIONS` methods, permits all request headers, allows credentials, and caches preflight responses for one hour.

Allowed origins should be restricted to trusted client applications in each deployment environment.

## 5. Exception Handling

Authentication exceptions are delegated to Spring's configured `HandlerExceptionResolver`, allowing security failures to use the application's centralized exception-handling mechanism.

The request authentication filter also delegates exceptions encountered during its processing to the resolver.

Error responses should provide sufficient information for clients to understand authentication failures without disclosing tokens, secrets, password hashes, or internal implementation details.

## 6. Security Considerations

The current implementation has several security-sensitive behaviors that should be reviewed and tested:

- **Public route fallback:** Unmatched HTTP routes are permitted by the request authorization configuration. Sensitive endpoints must be explicitly protected through request matchers or method-level authorization.
- **JWT validation:** Access and refresh token validation must verify the signature, expiration, and expected token type. Issuer validation should also be enforced where required by the deployment's trust model.
- **Refresh token lifecycle:** The presence of a `jti` claim does not itself provide token revocation or rotation. Those capabilities require additional state and validation logic if they are part of the security requirements.
- **Password encoding:** The configured PBKDF2 parameters and password-encoding behavior should be reviewed against current security requirements. Existing password hashes and migration requirements should be considered before changing the encoder configuration.
- **CORS and credentials:** Credentialed cross-origin requests should be limited to explicitly trusted origins. CORS is a browser access policy, not an authentication or authorization mechanism.
- **Transactional and authorization boundaries:** Sensitive operations must enforce authorization independently of whether a user is authenticated.

Security configuration and authorization rules should be covered by tests, including invalid and expired tokens, insufficient roles, access to protected endpoints, and attempts to access resources belonging to other users.