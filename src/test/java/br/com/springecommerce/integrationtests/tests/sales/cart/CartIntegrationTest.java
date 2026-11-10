package br.com.springecommerce.integrationtests.tests.sales.cart;

import br.com.springecommerce.catalog.brand.domain.model.Brand;
import br.com.springecommerce.catalog.category.domain.model.Category;
import br.com.springecommerce.catalog.product.domain.exception.ProductErrors;
import br.com.springecommerce.catalog.product.domain.model.Product;
import br.com.springecommerce.customer.customer.domain.model.Customer;
import br.com.springecommerce.iam.user.domain.model.User;
import br.com.springecommerce.integrationtests.config.TestConfigs;
import br.com.springecommerce.integrationtests.fixture.*;
import br.com.springecommerce.integrationtests.infrastructure.AbstractIntegrationTest;
import br.com.springecommerce.integrationtests.support.DatabaseCleaner;
import br.com.springecommerce.integrationtests.support.TestAuthenticator;
import br.com.springecommerce.inventory.stock.domain.exception.StockErrors;
import br.com.springecommerce.inventory.stock.domain.model.Stock;
import br.com.springecommerce.sales.cart.application.dto.CartAddProductRequestDto;
import br.com.springecommerce.sales.cart.application.dto.CartResponseDto;
import br.com.springecommerce.sales.cart.application.snapshot.CartItemSnapshot;
import br.com.springecommerce.sales.cart.domain.exception.CartErrors;
import br.com.springecommerce.sales.cart.domain.model.Cart;
import br.com.springecommerce.sales.cart.domain.status.CartStatus;
import br.com.springecommerce.sales.cart.infrastructure.persistence.repository.CartRepository;
import br.com.springecommerce.shared.exception.code.ErrorCode;
import br.com.springecommerce.shared.exception.response.ExceptionResponse;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class CartIntegrationTest extends AbstractIntegrationTest {

    private final CartRepository cartRepository;

    private final UserFixture userFixture;
    private final CustomerFixture customerFixture;
    private final CategoryFixture categoryFixture;
    private final BrandFixture brandFixture;
    private final ProductFixture productFixture;
    private final StockFixture stockFixture;
    private final CartFixture cartFixture;

    private final TestAuthenticator authenticator;
    private final DatabaseCleaner databaseCleaner;

    private static RequestSpecification specification;

    public CartIntegrationTest(
            CartRepository cartRepository,

            UserFixture userFixture,
            CustomerFixture customerFixture,
            CategoryFixture categoryFixture,
            BrandFixture brandFixture,
            ProductFixture productFixture,
            StockFixture stockFixture,
            CartFixture cartFixture,

            TestAuthenticator authenticator,
            DatabaseCleaner databaseCleaner
    ) {
        this.cartRepository = cartRepository;

        this.userFixture = userFixture;
        this.customerFixture = customerFixture;
        this.categoryFixture = categoryFixture;
        this.brandFixture = brandFixture;
        this.productFixture = productFixture;
        this.stockFixture = stockFixture;
        this.cartFixture = cartFixture;

        this.authenticator = authenticator;
        this.databaseCleaner = databaseCleaner;
    }

    @BeforeAll
    void setUp() {
        specification = new RequestSpecBuilder()
                .setBasePath("/api/v1/carts")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @AfterEach
    void tearDownScenario() {
        databaseCleaner.clean();
    }

    @Test
    void shouldReturnTheExistingActiveCartWithItsItems() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        BigDecimal productSalePrice = new BigDecimal("100.00");
        BigDecimal productPrice = new BigDecimal("120.00");

        Integer cartItemQuantity = 2;

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        Category category = categoryFixture.create();
        Brand brand = brandFixture.create();

        Product product = productFixture
                .withSalePrice(productSalePrice)
                .withPrice(productPrice)
                .withCategory(category)
                .withBrand(brand)
                .create();

        Cart cart = cartFixture.create(customer);
        cartFixture.addProduct(cart, product, cartItemQuantity);

        String token = authenticator.authenticate(email, password);

        // WHEN
        CartResponseDto response = given(specification)
                .auth()
                .oauth2(token)
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .as(CartResponseDto.class);

        // ASSERT
        assertThat(response.id())
                .isEqualTo(cart.getId());

        assertThat(response.customerId())
                .isEqualTo(customer.getId());

        assertThat(response.items())
                .hasSize(1);

        var item = response.items().get(0);

        assertThat(item.productId())
                .isEqualTo(product.getId());

        assertThat(item.quantity())
                .isEqualTo(cartItemQuantity);

        assertThat(item.unitPrice())
                .isEqualByComparingTo(productSalePrice);

        assertThat(response.subTotalAmount())
                .isEqualByComparingTo(
                        productSalePrice.multiply(
                                BigDecimal.valueOf(cartItemQuantity)
                        )
                );
    }

    @Test
    void shouldReturnAnInMemoryCartWhenNoActiveCartExists() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        String token = authenticator.authenticate(email, password);

        // WHEN
        CartResponseDto response = given(specification)
                .auth()
                .oauth2(token)
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .as(CartResponseDto.class);

        // ASSERT
        assertThat(response.id())
                .isNotNull();

        assertThat(response.customerId())
                .isEqualTo(customer.getId());

        assertThat(response.items())
                .isEmpty();

        assertThat(response.subTotalAmount())
                .isEqualByComparingTo(BigDecimal.ZERO);

        assertThat(cartRepository.findById(response.id()))
                .isEmpty();
    }

    @Test
    void shouldReturnAnInMemoryCartWhenOnlyAConvertedCartExists() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        Cart convertedCart = cartFixture.create(customer);
        convertedCart.setStatus(CartStatus.CONVERTED);
        cartRepository.save(convertedCart);

        String token = authenticator.authenticate(email, password);

        // WHEN
        CartResponseDto response = given(specification)
                .auth()
                .oauth2(token)
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .as(CartResponseDto.class);

        // ASSERT
        assertThat(response.id())
                .isNotNull();

        assertThat(response.id())
                .isNotEqualTo(convertedCart.getId());

        assertThat(response.customerId())
                .isEqualTo(customer.getId());

        assertThat(response.items())
                .isEmpty();

        assertThat(response.subTotalAmount())
                .isEqualByComparingTo(BigDecimal.ZERO);

        assertThat(cartRepository.findById(convertedCart.getId()))
                .isPresent()
                .get()
                .extracting(Cart::getStatus)
                .isEqualTo(CartStatus.CONVERTED);

        assertThat(cartRepository.findById(response.id()))
                .isEmpty();
    }

    @Test
    void shouldCreateAnActiveCartAndAddTheFirstProductWhenNoActiveCartExists() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        BigDecimal productSalePrice = new BigDecimal("100.00");
        BigDecimal productPrice = new BigDecimal("120.00");
        Integer stockAvailableQuantity = 100;
        Integer cartItemQuantity = 2;

        BigDecimal expectedSubtotal = productSalePrice
                .multiply(BigDecimal.valueOf(cartItemQuantity));

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        Category category = categoryFixture.create();
        Brand brand = brandFixture.create();

        Product product = productFixture
                .withSalePrice(productSalePrice)
                .withPrice(productPrice)
                .withCategory(category)
                .withBrand(brand)
                .create();

        Stock stock = stockFixture
                .withProduct(product)
                .withAvailableQuantity(stockAvailableQuantity)
                .create();

        String token = authenticator.authenticate(email, password);

        CartAddProductRequestDto request = new CartAddProductRequestDto(
                product.getId(),
                cartItemQuantity
        );

        // WHEN
        given(specification)
                .auth()
                .oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .post("/items")
                .then()
                .statusCode(204);

        // ASSERT
        Cart createdCart = cartRepository
                .findByCustomerIdAndStatus(customer.getId(), CartStatus.ACTIVE)
                .orElseThrow();

        List<CartItemSnapshot> items = cartRepository.findCartItems(createdCart.getId());

        assertThat(createdCart.getCustomerId())
                .isEqualTo(customer.getId());

        assertThat(createdCart.getStatus())
                .isEqualTo(CartStatus.ACTIVE);

        assertThat(items)
                .hasSize(1);

        CartItemSnapshot item = items
                .iterator()
                .next();

        assertThat(item.productId())
                .isEqualTo(product.getId());

        assertThat(item.quantity())
                .isEqualTo(cartItemQuantity);

        assertThat(item.unitPrice())
                .isEqualByComparingTo(productSalePrice);

        assertThat(createdCart.getSubtotalAmount())
                .isEqualByComparingTo(expectedSubtotal);
    }

    @Test
    void shouldAddAProductToTheExistingActiveCart() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        BigDecimal productSalePrice = new BigDecimal("100.00");
        BigDecimal productPrice = new BigDecimal("120.00");
        Integer stockAvailableQuantity = 100;
        Integer cartItemQuantity = 2;

        BigDecimal expectedSubtotal = productSalePrice
                .multiply(BigDecimal.valueOf(cartItemQuantity));

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        Category category = categoryFixture.create();
        Brand brand = brandFixture.create();

        Product product = productFixture
                .withSalePrice(productSalePrice)
                .withPrice(productPrice)
                .withCategory(category)
                .withBrand(brand)
                .create();

        stockFixture
                .withProduct(product)
                .withAvailableQuantity(stockAvailableQuantity)
                .create();

        Cart cart = cartFixture.create(customer);

        String token = authenticator.authenticate(email, password);

        CartAddProductRequestDto request = new CartAddProductRequestDto(
                product.getId(),
                cartItemQuantity
        );

        // WHEN
        given(specification)
                .auth()
                .oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .post("/items")
                .then()
                .statusCode(204);

        // ASSERT
        Cart updatedCart = cartRepository
                .findById(cart.getId())
                .orElseThrow();

        List<CartItemSnapshot> items = cartRepository.findCartItems(updatedCart.getId());

        assertThat(updatedCart.getId())
                .isEqualTo(cart.getId());

        assertThat(updatedCart.getCustomerId())
                .isEqualTo(customer.getId());

        assertThat(updatedCart.getStatus())
                .isEqualTo(CartStatus.ACTIVE);

        assertThat(items)
                .hasSize(1);

        CartItemSnapshot item = items
                .iterator()
                .next();

        assertThat(item.productId())
                .isEqualTo(product.getId());

        assertThat(item.quantity())
                .isEqualTo(cartItemQuantity);

        assertThat(item.unitPrice())
                .isEqualByComparingTo(productSalePrice);

        assertThat(updatedCart.getSubtotalAmount())
                .isEqualByComparingTo(expectedSubtotal);
    }

    @Test
    void shouldUpdateTheExistingCartItemWhenTheProductIsAlreadyInTheCart() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        BigDecimal initialSalePrice = new BigDecimal("100.00");
        BigDecimal updatedSalePrice = new BigDecimal("90.00");
        BigDecimal productPrice = new BigDecimal("120.00");

        Integer initialQuantity = 2;
        Integer updatedQuantity = 5;
        Integer stockAvailableQuantity = 100;

        BigDecimal expectedSubtotal = updatedSalePrice
                .multiply(BigDecimal.valueOf(updatedQuantity));

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        Category category = categoryFixture.create();
        Brand brand = brandFixture.create();

        Product product = productFixture
                .withSalePrice(initialSalePrice)
                .withPrice(productPrice)
                .withCategory(category)
                .withBrand(brand)
                .create();

        stockFixture
                .withProduct(product)
                .withAvailableQuantity(stockAvailableQuantity)
                .create();

        Cart cart = cartFixture.create(customer);
        cartFixture.addProduct(cart, product, initialQuantity);

        productFixture.updateSalePrice(product, updatedSalePrice);

        String token = authenticator.authenticate(email, password);

        CartAddProductRequestDto request = new CartAddProductRequestDto(
                product.getId(),
                updatedQuantity
        );

        // WHEN
        given(specification)
                .auth()
                .oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .post("/items")
                .then()
                .statusCode(204);

        // ASSERT
        Cart updatedCart = cartRepository
                .findById(cart.getId())
                .orElseThrow();

        List<CartItemSnapshot> items = cartRepository.findCartItems(updatedCart.getId());

        assertThat(items)
                .hasSize(1);

        CartItemSnapshot item = items
                .iterator()
                .next();

        assertThat(item.productId())
                .isEqualTo(product.getId());

        assertThat(item.quantity())
                .isEqualTo(updatedQuantity);

        assertThat(item.unitPrice())
                .isEqualByComparingTo(updatedSalePrice);

        assertThat(updatedCart.getSubtotalAmount())
                .isEqualByComparingTo(expectedSubtotal);
    }

    @Test
    void shouldFailToAddProductWhenRequestedQuantityExceedsAvailableStock() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        BigDecimal productSalePrice = new BigDecimal("100.00");
        BigDecimal productPrice = new BigDecimal("120.00");

        Integer stockAvailableQuantity = 10;
        Integer requestedQuantity = 11;

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        Category category = categoryFixture.create();
        Brand brand = brandFixture.create();

        Product product = productFixture
                .withSalePrice(productSalePrice)
                .withPrice(productPrice)
                .withCategory(category)
                .withBrand(brand)
                .create();

        stockFixture
                .withProduct(product)
                .withAvailableQuantity(stockAvailableQuantity)
                .create();

        String token = authenticator.authenticate(email, password);

        CartAddProductRequestDto request = new CartAddProductRequestDto(
                product.getId(),
                requestedQuantity
        );

        // WHEN
        ExceptionResponse response = given(specification)
                .auth()
                .oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .post("/items")
                .then()
                .statusCode(409)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .as(ExceptionResponse.class);

        // ASSERT
        assertThat(response.code())
                .isEqualTo(ErrorCode.INSUFFICIENT_STOCK.name());

        assertThat(response.errors().get(0).target())
                .isEqualTo(StockErrors.Target.QUANTITY);

        assertThat(response.errors().get(0).code())
                .isEqualTo(StockErrors.Code.STOCK_QUANTITY_INSUFFICIENT);
    }

    @Test
    void shouldThrowWhenProductIsNotAvailable() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        Integer requestedQuantity = 2;

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        Category category = categoryFixture.create();
        Brand brand = brandFixture.create();

        Product product = productFixture
                .withCategory(category)
                .withBrand(brand)
                .create();

        String token = authenticator.authenticate(email, password);

        CartAddProductRequestDto request = new CartAddProductRequestDto(
                product.getId(),
                requestedQuantity
        );

        // WHEN
        ExceptionResponse response = given(specification)
                .auth()
                .oauth2(token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .post("/items")
                .then()
                .statusCode(404)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .as(ExceptionResponse.class);

        // ASSERT
        assertThat(response.code())
                .isEqualTo(ErrorCode.NOT_FOUND.name());

        assertThat(response.errors().get(0).target())
                .isEqualTo(ProductErrors.Target.ID);

        assertThat(response.errors().get(0).code())
                .isEqualTo(ProductErrors.Code.PRODUCT_NOT_FOUND);
    }

    @Test
    void shouldRemoveProductSuccessfully() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        BigDecimal productSalePrice = new BigDecimal("100.00");
        BigDecimal productPrice = new BigDecimal("120.00");

        Integer cartItemQuantity = 2;

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        Category category = categoryFixture.create();
        Brand brand = brandFixture.create();

        Product product = productFixture
                .withSalePrice(productSalePrice)
                .withPrice(productPrice)
                .withCategory(category)
                .withBrand(brand)
                .create();



        Cart cart = cartFixture.create(customer);
        cartFixture.addProduct(cart, product, cartItemQuantity);

        String token = authenticator.authenticate(email, password);

        // WHEN
        given(specification)
                .auth()
                .oauth2(token)
                .delete("/items/product/{productId}", product.getId())
                .then()
                .statusCode(204);

        // ASSERT
        CartResponseDto response = given(specification)
                .auth()
                .oauth2(token)
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .as(CartResponseDto.class);

        assertThat(response.id())
                .isEqualTo(cart.getId());

        assertThat(response.customerId())
                .isEqualTo(customer.getId());

        assertThat(response.items())
                .isEmpty();

        assertThat(response.subTotalAmount())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldThrowWhenRemoveProductAndItIsNotInCart() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        Category category = categoryFixture.create();
        Brand brand = brandFixture.create();

        Product product = productFixture
                .withCategory(category)
                .withBrand(brand)
                .create();

        Cart cart = cartFixture.create(customer);

        String token = authenticator.authenticate(email, password);

        // WHEN
        ExceptionResponse response = given(specification)
                .auth()
                .oauth2(token)
                .delete("/items/product/{productId}", product.getId())
                .then()
                .statusCode(404)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .as(ExceptionResponse.class);

        // ASSERT
        assertThat(response.code())
                .isEqualTo(ErrorCode.NOT_FOUND.name());

        assertThat(response.errors().get(0).target())
                .isEqualTo(CartErrors.Target.ITEM);

        assertThat(response.errors().get(0).code())
                .isEqualTo(CartErrors.Code.CART_ITEM_NOT_FOUND);
    }

    @Test
    void shouldThrowWhenWhenRemoveProductAndActiveCartDoesNotExist() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        UUID productId = UUID.randomUUID();

        String token = authenticator.authenticate(email, password);

        // WHEN
        ExceptionResponse response = given(specification)
                .auth()
                .oauth2(token)
                .delete("/items/product/{productId}", productId)
                .then()
                .statusCode(404)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .as(ExceptionResponse.class);

        // ASSERT
        assertThat(response.code())
                .isEqualTo(ErrorCode.NOT_FOUND.name());

        assertThat(response.errors().get(0).target())
                .isEqualTo(CartErrors.Target.CART);

        assertThat(response.errors().get(0).code())
                .isEqualTo(CartErrors.Code.CART_NOT_FOUND);
    }

    @Test
    void shouldRemoveProductAndKeepOtherItems() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        String firstProductTitle = "Fist Product Teste";
        String firstProductSlug = "first-product-slug";
        String firstProductSku = "SKU-001";
        String firstProductBarcode = "7891234567890";
        Integer firstProductQuantity = 2;

        String secondProductTitle = "Second Product Teste";
        String secondProductSlug = "second-product-slug";
        String secondProductSku = "SKU-002";
        String secondProductBarcode = "7891234567891";
        Integer secondProductQuantity = 3;

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        Category category = categoryFixture.create();
        Brand brand = brandFixture.create();

        Product firstProduct = productFixture
                .withTitle(firstProductTitle)
                .withSlug(firstProductSlug)
                .withSku(firstProductSku)
                .withBarcode(firstProductBarcode)
                .withCategory(category)
                .withBrand(brand)
                .create();

        Product secondProduct = productFixture
                .withTitle(secondProductTitle)
                .withSlug(secondProductSlug)
                .withSku(secondProductSku)
                .withBarcode(secondProductBarcode)
                .withCategory(category)
                .withBrand(brand)
                .create();

        Cart cart = cartFixture.create(customer);
        cartFixture.addProduct(cart, firstProduct, firstProductQuantity);
        cartFixture.addProduct(cart, secondProduct, secondProductQuantity);

        String token = authenticator.authenticate(email, password);

        // WHEN
        given(specification)
                .auth()
                .oauth2(token)
                .delete("/items/product/{productId}", firstProduct.getId())
                .then()
                .statusCode(204);

        // ASSERT
        CartResponseDto response = given(specification)
                .auth()
                .oauth2(token)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .as(CartResponseDto.class);

        assertThat(response.items())
                .hasSize(1);

        assertThat(response.items().get(0).productId())
                .isEqualTo(secondProduct.getId());

        assertThat(response.items().get(0).quantity())
                .isEqualTo(secondProductQuantity);
    }

    @Test
    void shouldRemoveLastProductAndResetSubtotal() {
        // GIVEN
        String email = "user@test.com";
        String password = "password";

        BigDecimal productSalePrice = new BigDecimal("100.00");
        BigDecimal productPrice = new BigDecimal("120.00");

        Integer cartItemQuantity = 2;

        User user = userFixture
                .withEmail(email)
                .withPassword(password)
                .create();

        Customer customer = customerFixture.create(user);

        Category category = categoryFixture.create();
        Brand brand = brandFixture.create();

        Product product = productFixture
                .withSalePrice(productSalePrice)
                .withPrice(productPrice)
                .withCategory(category)
                .withBrand(brand)
                .create();

        Cart cart = cartFixture.create(customer);
        cartFixture.addProduct(cart, product, cartItemQuantity);

        String token = authenticator.authenticate(email, password);

        // WHEN
        given(specification)
                .auth()
                .oauth2(token)
                .delete("/items/product/{productId}", product.getId())
                .then()
                .statusCode(204);

        // ASSERT
        CartResponseDto response = given(specification)
                .auth()
                .oauth2(token)
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .as(CartResponseDto.class);

        assertThat(response.id())
                .isEqualTo(cart.getId());

        assertThat(response.customerId())
                .isEqualTo(customer.getId());

        assertThat(response.items())
                .isEmpty();

        assertThat(response.subTotalAmount())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }
}