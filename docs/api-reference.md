# API Reference

## Overview

SpringEcommerce exposes a REST API organized into five business modules: IAM, Catalog, Customer, Inventory, and Sales.

Interactive documentation is available through Swagger UI.

- **Swagger UI:** `/swagger-ui/index.html`
- **OpenAPI Specification:** `/v3/api-docs`

## Authentication

The API uses JSON Web Tokens (JWT) for authentication. Access to protected operations is governed by the application's authorization policies.

## IAM — Identity and Access Management

### Users

| Method | Endpoint | Description |
|--------| --- | --- |
| GET    | `/api/v1/users` | Retrieve a paginated list of users |
| GET    | `/api/v1/users/{id}` | Retrieve a user by ID |
| POST   | `/api/v1/users` | Create a new user |
| POST   | `/api/v1/users/employee` | Create a new employee |
| PUT    | `/api/v1/users/assignRole` | Assign a role to a user |
| PUT    | `/api/v1/users/removeRole` | Remove a role from a user |
| PATCH   | `/api/v1/users/{id}/deactivate` | Deactivate a user account |
| PATCH    | `/api/v1/users/{id}/activate` | Activate a previously deactivated user account |
| DELETE | `/api/v1/users/{id}` | Delete a user by ID |

### Roles

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/v1/roles` | Retrieve paginated roles |
| GET | `/api/v1/roles/{id}` | Retrieve a role by ID |
| GET | `/api/v1/roles/title/{title}` | Retrieve a role by title |
| POST | `/api/v1/roles` | Create a new role |
| PUT | `/api/v1/roles/{id}` | Update a role by ID |

### Authentication

| Method | Endpoint               | Description |
| --- |------------------------| --- |
| POST | `/api/v1/auth/signin`  | Authenticate a user and generate an access token |
| POST | `/api/v1/auth/refresh` | Refresh an access token using a refresh token |

## Catalog

### Products

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/v1/products` | Retrieve a paginated list of products with public information and filtering options |
| GET | `/api/v1/products/slug/{slug}` | Retrieve a product by its slug |
| GET | `/api/v1/products/manager` | Retrieve a paginated list of products with stock information and filtering options (MANAGER only) |
| GET | `/api/v1/products/manager/{id}` | Retrieve a product with stock information by ID (MANAGER only) |
| GET | `/api/v1/products/manager/barcode/{barcode}` | Retrieve a product with stock information by barcode (MANAGER only) |
| GET | `/api/v1/products/manager/sku/{sku}` | Retrieve a product with stock information by SKU (MANAGER only) |
| POST | `/api/v1/products/manager` | Create a new product (MANAGER only) |
| PUT | `/api/v1/products/manager/{id}` | Update an existing product by ID (MANAGER only) |
| PATCH | `/api/v1/products/manager/{id}/deactivate` | Deactivate a product by ID (MANAGER only) |
| PATCH | `/api/v1/products/manager/{id}/activate` | Activate a previously deactivated product by ID (MANAGER only) |

Product search supports dynamic filtering using Spring Data JPA Specifications and pagination where configured.

### Categories

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/v1/categories` | Retrieve a list of publicly available categories |
| GET | `/api/v1/categories/slug/{slug}` | Retrieve a public category by its slug |
| GET | `/api/v1/categories/manager` | Retrieve a list of all categories (MANAGER only) |
| GET | `/api/v1/categories/manager/{id}` | Retrieve a category by ID (MANAGER only) |
| POST | `/api/v1/categories/manager` | Create a new category (MANAGER only) |
| PUT | `/api/v1/categories/manager/{id}` | Update an existing category by ID (MANAGER only) |
| PATCH | `/api/v1/categories/manager/{id}/deactivate` | Deactivate a category by ID (MANAGER only) |
| PATCH | `/api/v1/categories/manager/{id}/activate` | Activate a previously deactivated category by ID (MANAGER only) |

### Brands

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/v1/brands` | Retrieve a list of publicly available brands |
| GET | `/api/v1/brands/slug/{slug}` | Retrieve a public brand by its slug |
| GET | `/api/v1/brands/manager` | Retrieve a list of all brands (MANAGER only) |
| GET | `/api/v1/brands/manager/{id}` | Retrieve a brand by ID (MANAGER only) |
| POST | `/api/v1/brands/manager` | Create a new brand (MANAGER only) |
| PUT | `/api/v1/brands/manager/{id}` | Update an existing brand by ID (MANAGER only) |
| PATCH | `/api/v1/brands/manager/{id}/deactivate` | Deactivate a brand by ID (MANAGER only) |
| PATCH | `/api/v1/brands/manager/{id}/activate` | Activate a previously deactivated brand by ID (MANAGER only) |

## Customer

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/v1/customers` | Retrieve a paginated list of customers (ADMIN only) |
| GET | `/api/v1/customers/{id}` | Retrieve a customer by ID (ADMIN only) |
| GET | `/api/v1/customers/me` | Retrieve the authenticated customer's profile (CUSTOMER only) |
| POST | `/api/v1/customers` | Create a customer profile for the authenticated user |
| PUT | `/api/v1/customers/{id}` | Update a customer's information by ID (ADMIN or the customer themselves) |
| DELETE | `/api/v1/customers/{id}` | Delete a customer by ID (ADMIN or the customer themselves) |

## Inventory

### Stock

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/v1/stocks` | Create a new stock record for a product (MANAGER only) |
| GET | `/api/v1/stocks` | Retrieve a paginated list of stock records with filtering options (MANAGER only) |
| GET | `/api/v1/stocks/{id}` | Retrieve stock details by ID (MANAGER only) |
| PATCH | `/api/v1/stocks/{id}/minimum-stock` | Update the minimum stock quantity threshold (MANAGER only) |
| PATCH | `/api/v1/stocks/{id}/increase` | Increase the available stock quantity (MANAGER only) |
| PATCH | `/api/v1/stocks/{id}/write-off` | Write off a quantity from available stock (MANAGER only) |
| PATCH | `/api/v1/stocks/{id}/reserve` | Reserve a quantity of stock (MANAGER only) |
| PATCH | `/api/v1/stocks/{id}/release` | Release a previously reserved stock quantity (MANAGER only) |

Stock updates use conditional database operations to help preserve stock quantity constraints.

## Sales

### Cart

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/v1/carts` | Retrieve the authenticated user's shopping cart |
| POST | `/api/v1/carts/items` | Add a product to the authenticated user's shopping cart |
| DELETE | `/api/v1/carts/items/product/{productId}` | Remove a product from the authenticated user's shopping cart by product ID |

### Checkout

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/v1/checkout` | Finalize the authenticated user's shopping cart and create a new order |

Checkout coordinates cart validation, inventory operations, and order creation within a transactional workflow.

### Orders

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/v1/orders/{id}` | Retrieve an order by ID belonging to the authenticated user |
| GET | `/api/v1/orders` | Retrieve a paginated list of orders belonging to the authenticated user, sorted by creation date in descending order |
| PUT | `/api/v1/orders/{id}/cancel` | Cancel an order belonging to the authenticated user |

## Common API Conventions

### Pagination

Paginated endpoints return results using the application's standard pagination response format, `PageResponse<T>`.

Pagination and sorting parameters are supported where applicable. The available parameters and default values are documented for each endpoint.

### Error Responses

Errors are handled through a centralized exception-handling mechanism and returned using a consistent response format.

HTTP status codes indicate the outcome of a request. Validation errors, resource not found errors, conflicts, authentication failures, and authorization failures are reported using appropriate status codes.

### Authorization

Endpoints may be public or require authentication, depending on their security configuration.

Protected endpoints require a valid Bearer token in the `Authorization` header. Access is restricted according to the roles and permissions configured for each endpoint.

### Request and Response Formats

Endpoints that accept or return structured data use JSON (`application/json`), unless otherwise specified.

Request bodies are validated according to the constraints defined by the application.

### Resource Identification

Resources are identified by their unique IDs, typically represented as UUIDs. Some resources may also be retrieved using alternative identifiers, such as slugs, SKUs, or barcodes, where supported.