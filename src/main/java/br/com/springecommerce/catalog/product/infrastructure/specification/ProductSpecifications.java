package br.com.springecommerce.catalog.product.infrastructure.specification;

import br.com.springecommerce.catalog.brand.domain.model.Brand;
import br.com.springecommerce.catalog.category.domain.model.Category;
import br.com.springecommerce.inventory.stock.domain.model.Stock;
import br.com.springecommerce.catalog.product.domain.model.Product;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductSpecifications {

    public static Specification<Product> search(String search) {
        return (root, query, cb) -> {

            if (search == null || search.isBlank()) {
                return null;
            }

            String term = "%" + search.toLowerCase().trim() + "%";

            Join<Product, Brand> brand = root.join("brand", JoinType.LEFT);
            Join<Product, Category> category = root.join("category", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("title")), term),
                    cb.like(cb.lower(root.get("slug")), term),
                    cb.like(cb.lower(root.get("description")), term),
                    cb.like(cb.lower(root.get("sku")), term),
                    cb.like(cb.lower(brand.get("title")), term),
                    cb.like(cb.lower(category.get("title")), term)
            );
        };
    }

    public static Specification<Product> active(Boolean active) {
        return (root, query, cb) ->
                active == null
                        ? null
                        : cb.equal(root.get("active"), active);
    }

    public static Specification<Product> featured(Boolean featured) {
        return (root, query, cb) ->
                featured == null
                        ? null
                        : cb.equal(root.get("featured"), featured);
    }

    public static Specification<Product> isNew(Boolean isNew) {
        return (root, query, cb) -> {

            if (isNew == null) {
                return null;
            }

            LocalDateTime limit = LocalDateTime.now().minusDays(30);

            return isNew
                    ? cb.greaterThanOrEqualTo(root.get("createdAt"), limit)
                    : cb.lessThan(root.get("createdAt"), limit);
        };
    }

    public static Specification<Product> category(UUID categoryId) {
        return (root, query, cb) ->
                categoryId == null
                        ? null
                        : cb.equal(root.join("category").get("id"), categoryId);
    }

    public static Specification<Product> brand(UUID brandId) {
        return (root, query, cb) ->
                brandId == null
                        ? null
                        : cb.equal(root.join("brand").get("id"), brandId);
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) {
                return null;
            }

            Expression<BigDecimal> effectivePrice = cb.coalesce(root.get("salePrice"), root.get("price"));

            return cb.greaterThanOrEqualTo(effectivePrice, minPrice);
        };
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) {
                return null;
            }

            Expression<BigDecimal> effectivePrice = cb.coalesce(root.get("salePrice"), root.get("price"));

            return cb.lessThanOrEqualTo(effectivePrice, maxPrice);
        };
    }

    public static Specification<Product> minCost(BigDecimal minCost) {
        return (root, query, cb) ->
                minCost == null
                        ? null
                        : cb.greaterThanOrEqualTo(root.get("costPrice"), minCost);
    }

    public static Specification<Product> maxCost(BigDecimal maxCost) {
        return (root, query, cb) ->
                maxCost == null
                        ? null
                        : cb.lessThanOrEqualTo(root.get("costPrice"), maxCost);
    }

    public static Specification<Product> availableForSale(Boolean availableForSale) {
        return (root, query, cb) -> {

            if (availableForSale == null) {
                return null;
            }

            Subquery<Integer> stockSubquery = query.subquery(Integer.class);

            Root<Stock> stock = stockSubquery.from(Stock.class);

            stockSubquery.select(cb.literal(1));

            stockSubquery.where(
                    cb.equal(stock.get("productId"), root.get("id")),
                    cb.gt(stock.get("availableQuantity"), 0)
            );

            Predicate isActive = cb.isTrue(root.get("active"));
            Predicate hasAvailableStock = cb.exists(stockSubquery);

            return availableForSale
                    ? cb.and(isActive, hasAvailableStock)
                    : cb.or(
                    cb.isFalse(root.get("active")),
                    cb.not(hasAvailableStock)
            );
        };
    }

    public static Specification<Product> hasAvailableStock(Boolean hasStock) {
        return (root, query, cb) -> {

            if (hasStock == null) {
                return null;
            }

            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<Stock> stock = subquery.from(Stock.class);

            subquery.select(cb.literal(1));
            subquery.where(
                    cb.equal(stock.get("productId"), root.get("id")),
                    hasStock
                            ? cb.gt(stock.get("availableQuantity"), 0)
                            : cb.le(stock.get("availableQuantity"), 0)
            );

            return hasStock
                    ? cb.exists(subquery)
                    : cb.not(cb.exists(subquery));
        };
    }

    public static Specification<Product> onSale(Boolean onSale) {
        return (root, query, cb) -> {
            if (onSale == null) {
                return null;
            }

            return onSale
                    ? cb.lessThan(root.get("salePrice"), root.get("price"))
                    : cb.or(
                            cb.isNull(root.get("salePrice")),
                            cb.greaterThanOrEqualTo(root.get("salePrice"), root.get("price"))
                    );
        };
    }

    public static Specification<Product> createdFrom(LocalDateTime createdFrom) {
        return (root, query, cb) ->
            createdFrom == null
                ? null
                : cb.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    createdFrom
                );
    }

    public static Specification<Product> createdTo(LocalDateTime createdTo) {
        return (root, query, cb) ->
            createdTo == null
                ? null
                : cb.lessThanOrEqualTo(
                    root.get("createdAt"),
                    createdTo
                );
    }

    public static Specification<Product> updatedFrom(LocalDateTime updatedFrom) {
        return (root, query, cb) ->
            updatedFrom == null
                ? null
                : cb.greaterThanOrEqualTo(
                    root.get("updatedAt"),
                    updatedFrom
                );
    }

    public static Specification<Product> updatedTo(LocalDateTime updatedTo) {
        return (root, query, cb) ->
            updatedTo == null
                ? null
                : cb.lessThanOrEqualTo(
                    root.get("updatedAt"),
                    updatedTo
                );
    }
}

