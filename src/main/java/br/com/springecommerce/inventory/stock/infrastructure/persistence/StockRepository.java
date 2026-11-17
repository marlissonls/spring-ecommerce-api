package br.com.springecommerce.inventory.stock.infrastructure.persistence;

import br.com.springecommerce.inventory.stock.application.snapshot.StockSnapshot;
import br.com.springecommerce.inventory.stock.domain.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID>, JpaSpecificationExecutor<Stock> {

    Optional<Stock> findByProductId(UUID productId);

    @Modifying
    @Query("""
        UPDATE Stock i
            SET i.minimumQuantity = :newMinimumQuantity
        WHERE i.id = :id
    """)
    int updateMinimumQuantity(
        @Param("id") UUID id,
        @Param("newMinimumQuantity") Integer newMinimumQuantity
    );

    @Modifying
    @Query("""
        UPDATE Stock i
            SET i.availableQuantity = i.availableQuantity + :amount
        WHERE i.id = :id
    """)
    int increase(
        @Param("id") UUID id,
        @Param("amount") Integer amount
    );

    @Modifying
    @Query("""
        UPDATE Stock i
            SET i.availableQuantity = i.availableQuantity - :amount
        WHERE i.id = :id
            AND i.availableQuantity >= :amount
    """)
    int writeOff(
        @Param("id") UUID id,
        @Param("amount") Integer amount
    );

    @Modifying
    @Query("""
        UPDATE Stock i
            SET i.availableQuantity = i.availableQuantity - :amount,
                i.reservedQuantity = i.reservedQuantity + :amount
        WHERE i.id = :id
            AND i.availableQuantity >= :amount
    """)
    int reserve(
        @Param("id") UUID id,
        @Param("amount") Integer amount
    );

    @Modifying
    @Query("""
        UPDATE Stock i
            SET i.availableQuantity = i.availableQuantity + :amount,
                i.reservedQuantity = i.reservedQuantity - :amount
        WHERE i.id = :id
            AND i.reservedQuantity >= :amount
    """)
    int release(
        @Param("id") UUID id,
        @Param("amount") Integer amount
    );

    @Modifying
    @Query("""
        UPDATE Stock i
            SET i.reservedQuantity = i.reservedQuantity - :amount
        WHERE i.id = :id
            AND i.reservedQuantity >= :amount
    """)
    int commit(
        @Param("id") UUID id,
        @Param("amount") Integer amount
    );

    List<Stock> findByProductIdIn(List<UUID> ids);

    @Query("""
        SELECT new br.com.springecommerce.inventory.stock.application.snapshot.StockSnapshot(
            s.id,
            s.productId,
            s.availableQuantity,
            s.reservedQuantity,
            s.minimumQuantity
        )
        FROM Stock s
        WHERE s.productId IN :productIds
    """)
    List<StockSnapshot> findSnapshotsByProductIdIn(@Param("productIds") List<UUID> productIds);
}
