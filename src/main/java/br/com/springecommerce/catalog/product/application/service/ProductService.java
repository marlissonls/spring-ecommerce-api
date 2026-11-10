package br.com.springecommerce.catalog.product.application.service;

import br.com.springecommerce.catalog.brand.application.service.BrandService;
import br.com.springecommerce.catalog.category.application.service.CategoryService;
import br.com.springecommerce.catalog.product.application.dto.*;
import br.com.springecommerce.catalog.product.application.snapshot.ProductSnapshot;
import br.com.springecommerce.catalog.product.application.snapshot.ProductStockSnapshot;
import br.com.springecommerce.catalog.product.domain.exception.ProductErrors;
import br.com.springecommerce.catalog.product.domain.exception.ProductUnavailableException;
import br.com.springecommerce.catalog.product.domain.model.Product;
import br.com.springecommerce.catalog.product.domain.valueobject.ProductImage;
import br.com.springecommerce.catalog.product.infrastructure.persistence.ProductRepository;
import br.com.springecommerce.catalog.product.infrastructure.specification.ProductSpecificationFactory;
import br.com.springecommerce.catalog.product.presentation.filter.ProductManagerFilter;
import br.com.springecommerce.catalog.product.presentation.filter.ProductPublicFilter;
import br.com.springecommerce.inventory.stock.application.service.StockQueryService;
import br.com.springecommerce.inventory.stock.domain.model.Stock;
import br.com.springecommerce.shared.exception.base.ConflictException;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
import br.com.springecommerce.shared.pagination.PageResponse;
import br.com.springecommerce.shared.utils.SlugUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository repository;

    private final StockQueryService stockQueryService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    public ProductService(
            ProductRepository repository,
            StockQueryService stockQueryService,
            CategoryService categoryService,
            BrandService brandService
    ) {
        this.repository = repository;
        this.stockQueryService = stockQueryService;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    public PageResponse<ProductPublicResponseDto> getPublicViewProducts(
            ProductPublicFilter filter, Pageable pageable
    ) {
        logger.info("Search public products. filter={}", filter);

        Page<Product> page = repository.findAll(
                ProductSpecificationFactory.publicView(filter),
                pageable
        );

        var productIds = getProductIds(page);

        var productImageByProductId = findMainProductImagesByProductIds(productIds);

        var stocksByProductId = findStocksByProductIds(productIds);

        var items = page.map(product -> new ProductPublicResponseDto(
                product,
                productImageByProductId.get(product.getId()),
                stocksByProductId.get(product.getId())
        )).getContent();

        return new PageResponse<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    public PageResponse<ProductManagerResponseDto> getManagerViewProducts(
            ProductManagerFilter filter, Pageable pageable
    ) {
        logger.debug("Fetching filtered products");

        Page<Product> page = repository.findAll(
                ProductSpecificationFactory.managerView(filter),
                pageable
        );

        var productIds = getProductIds(page);

        var productImageByProductId = findMainProductImagesByProductIds(productIds);

        var stocksByProductId = findStocksByProductIds(productIds);

        var items = page.map(product -> new ProductManagerResponseDto(
                product,
                productImageByProductId.get(product.getId()),
                stocksByProductId.get(product.getId())
        )).getContent();

        return new PageResponse<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    public ProductStockSnapshot getAvailableProduct(UUID id) {
        ProductSnapshot product = repository.findAvailableProduct(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ProductErrors.Target.ID,
                        ProductErrors.Code.PRODUCT_NOT_FOUND));

        Integer stock = stockQueryService.findAvailableQuantityByProductId(id);

        return new ProductStockSnapshot(product, stock);
    }

    public ProductManagerDetailsResponseDto findById(UUID id) {
        logger.debug("Fetching product id={}", id);

        var product = findEntityById(id);

        var productImages = findProductImagesByProductId(product.getId());

        var stock = getStock(product);

        return new ProductManagerDetailsResponseDto(product, productImages, stock);
    }

    public ProductPublicDetailsResponseDto findBySlug(String slug) {
        logger.debug("Fetching product slug={}", slug);

        var product = repository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ProductErrors.Target.SLUG,
                        ProductErrors.Code.PRODUCT_NOT_FOUND));

        var productImages = findProductImagesByProductId(product.getId());

        var stock = getStock(product);

        return new ProductPublicDetailsResponseDto(product, productImages, stock);
    }

    public ProductManagerDetailsResponseDto findByBarcode(String barcode) {
        logger.debug("Fetching product barcode={}", barcode);

        var product = repository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ProductErrors.Target.BARCODE,
                        ProductErrors.Code.PRODUCT_NOT_FOUND));

        var productImages = findProductImagesByProductId(product.getId());

        var stock = getStock(product);

        return new ProductManagerDetailsResponseDto(product, productImages, stock);
    }

    public ProductManagerDetailsResponseDto findBySku(String sku) {
        logger.debug("Fetching product sku={}", sku);

        var product = repository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ProductErrors.Target.SKU,
                        ProductErrors.Code.PRODUCT_NOT_FOUND));

        var productImages = findProductImagesByProductId(product.getId());

        var stock = getStock(product);

        return new ProductManagerDetailsResponseDto(product, productImages, stock);
    }

    @Transactional
    public ProductResponseDto create(ProductCreateDto dto) {
        logger.debug("Creating Product: {}", dto.title());

        try {
            String slug = SlugUtils.toSlug(dto.title());

            var category = categoryService.getValidReference(dto.categoryId());
            var brand = brandService.getValidReference(dto.brandId());

            Product product = Product.createFrom(dto, slug, category, brand);

            Product created = repository.save(product);

            logger.info("Product created successfully. product title={}", created.getTitle());

            return new ProductResponseDto(created);
        } catch (DataIntegrityViolationException ex) {
            handleUniqueConstraintViolation(ex);
            throw ex;
        }
    }

    @Transactional
    public ProductResponseDto update(UUID id, ProductUpdateDto dto) {
        logger.debug("Updating Product: {}", dto.title());

        Product product = findEntityById(id);

        var category = dto.categoryId() != null
                ? categoryService.getValidReference(dto.categoryId())
                : null;

        var brand = dto.brandId() != null
                ? brandService.getValidReference(dto.brandId())
                : null;

        product.update(dto, category, brand);

        Product updated = repository.save(product);

        logger.info("Product updated successfully. product title={}", updated.getTitle());

        return new ProductResponseDto(updated);
    }

    @Transactional
    public void deactivate(UUID id) {
        logger.debug("Deactivating Product id={}", id);

        validateProduct(id);

        repository.deactivate(id);

        logger.info("Product deactivated successfully. id={}", id);
    }

    @Transactional
    public void activate(UUID id) {
        logger.debug("Activating Product id={}", id);

        validateProduct(id);

        repository.activate(id);

        logger.info("Product activated successfully. id={}", id);
    }

    public void checkProductsAreActive(List<UUID> productIds) {
        List<Product> products = repository.findAllById(productIds);

        Map<UUID, Product> productMap = products.stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        Function.identity()
                ));

        for (UUID productId : productIds) {
            Product product = productMap.get(productId);

            if (product == null) {
                throw new ResourceNotFoundException(
                        ProductErrors.Target.ID,
                        ProductErrors.Code.PRODUCT_NOT_FOUND
                );
            }

            if (!product.getActive()) {
                throw new ProductUnavailableException(
                        ProductErrors.Target.PRODUCT,
                        ProductErrors.Code.PRODUCT_UNAVAILABLE
                );
            }
        }
    }

    private Product findEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ProductErrors.Target.PRODUCT, ProductErrors.Code.PRODUCT_NOT_FOUND));
    }

    private void handleUniqueConstraintViolation(
            DataIntegrityViolationException ex) {

        String message = ExceptionUtils.getRootCauseMessage(ex);

        if (message.contains("uk_product_title")) {
            throw new ConflictException(
                    ProductErrors.Target.TITLE,
                    ProductErrors.Code.PRODUCT_TITLE_ALREADY_EXISTS
            );
        }

        if (message.contains("uk_product_slug")) {
            throw new ConflictException(
                    ProductErrors.Target.SLUG,
                    ProductErrors.Code.PRODUCT_SLUG_ALREADY_EXISTS
            );
        }

        if (message.contains("uk_product_barcode")) {
            throw new ConflictException(
                    ProductErrors.Target.BARCODE,
                    ProductErrors.Code.PRODUCT_BARCODE_ALREADY_EXISTS
            );
        }

        if (message.contains("uk_product_sku")) {
            throw new ConflictException(
                    ProductErrors.Target.SKU,
                    ProductErrors.Code.PRODUCT_SKU_ALREADY_EXISTS
            );
        }
    }

    private void validateProduct(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    ProductErrors.Target.ID,
                    ProductErrors.Code.PRODUCT_NOT_FOUND
            );
        }
    }

    public Product getValidReference(UUID id) {
        logger.debug("Fetching product reference id={}", id);

        validateProduct(id);

        return repository.getReferenceById(id);
    }

    private @Nullable Stock getStock(Product product) {
        return stockQueryService.findByProductIdIn(List.of(product.getId()))
                .stream()
                .findFirst()
                .orElse(null);
    }

    private List<UUID> getProductIds(Page<Product> page) {
        return page.getContent()
                .stream()
                .map(Product::getId)
                .toList();
    }

    private List<ProductImage> findProductImagesByProductId(UUID productId) {
        return repository.findProductImagesByProductId(productId);
    }

    private Map<UUID, ProductImageResponseDto> findMainProductImagesByProductIds(
            List<UUID> productIds
    ) {
        return repository.findMainProductImagesByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> new ProductImageResponseDto((ProductImage) row[1])
                ));
    }

    private Map<UUID, Stock> findStocksByProductIds(List<UUID> productIds) {
        return stockQueryService.findByProductIdIn(productIds)
                .stream()
                .collect(Collectors.toMap(
                        Stock::getProductId,
                        Function.identity()
                ));
    }

    @Transactional
    public void addImages(UUID id, List<ProductImageCreateDto> images) {
        Product product = findEntityById(id);

        product.addImages(images);
    }

//    @Transactional
//    public void updateImages(UUID id, List<ProductImageCreateDto> images) {
//        Product product = findEntityById(id);
//
//        product.updateImages(images);
//    }
//
//    @Transactional
//    public void removeImages(UUID id, String publicId, List<ProductImageCreateDto> images) {
//        Product product = findEntityById(id);
//
//        product.removeImages(images);
//    }
}
