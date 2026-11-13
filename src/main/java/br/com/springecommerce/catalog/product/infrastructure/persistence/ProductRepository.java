package br.com.springecommerce.catalog.product.infrastructure.persistence;

import br.com.springecommerce.catalog.product.domain.model.Product;
import br.com.springecommerce.catalog.product.application.snapshot.ProductSnapshot;
import br.com.springecommerce.catalog.product.domain.valueobject.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository
        extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlugAndActiveTrue(String slug);

    Optional<Product> findByBarcode(String barcode);

    Optional<Product> findBySku(String sku);

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.active = false
        WHERE p.id = :id
        AND p.active
    """)
    int deactivate(@Param("id") UUID id);

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.active = true
        WHERE p.id = :id
        AND NOT p.active
    """)
    int activate(@Param("id") UUID id);

    @Query("""
        SELECT p.id, pi
        FROM Product p
        JOIN p.images pi
        WHERE p.id IN :productIds
        AND pi.main = true
    """)
    List<Object[]> findMainProductImagesByProductIds(
            @Param("productIds") List<UUID> productIds
    );

    @Query("""
        SELECT pi
        FROM Product p
        JOIN p.images pi
        WHERE p.id = :productId
    """)
    List<ProductImage> findProductImagesByProductId(
            @Param("productId") UUID productId
    );

    @Query("""
        SELECT new br.com.springecommerce.catalog.product.application.snapshot.ProductSnapshot(
            p.id,
            p.title,
            pi.url,
            p.price,
            p.salePrice,
            p.active
        )
        FROM Product p
        LEFT JOIN p.images pi
        WHERE p.id IN :productIds
        AND (pi.main = true OR pi IS NULL)
    """)
    List<ProductSnapshot> findByIdIn(
            @Param("productIds") List<UUID> productIds
    );
}
