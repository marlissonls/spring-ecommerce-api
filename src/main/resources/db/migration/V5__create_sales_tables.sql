CREATE TABLE IF NOT EXISTS carts (
    id CHAR(36) PRIMARY KEY,

    customer_id CHAR(36) NOT NULL,

    status ENUM(
        'ACTIVE',
        'LOCKED',
        'CONVERTED',
        'MERGED',
        'EXPIRED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_cart_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
) ENGINE=InnoDB;

--
CREATE TABLE IF NOT EXISTS cart_items (
    cart_id CHAR(36) NOT NULL,
    product_id CHAR(36) NOT NULL,

    quantity INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_quantity_positive CHECK (quantity > 0),

    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES carts (id),
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT uk_cart_product UNIQUE (cart_id, product_id),

    PRIMARY KEY (cart_id, product_id) -- Added because Render need it
) ENGINE=InnoDB;

--
CREATE TABLE IF NOT EXISTS orders (
    id CHAR(36) PRIMARY KEY,

    customer_id CHAR(36) NOT NULL,

    subtotal_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    shipping_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,

    status ENUM(
        'PENDING_PAYMENT',
        'PAYMENT_PROCESSING',
        'PAID',
        'PICKING',
        'PACKING',
        'READY_FOR_SHIPMENT',
        'SHIPPED',
        'OUT_FOR_DELIVERY',
        'DELIVERED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'PENDING_PAYMENT',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_order_subtotal_amount_positive CHECK (subtotal_amount >= 0),
    CONSTRAINT chk_order_discount_amount_positive CHECK (discount_amount >= 0),
    CONSTRAINT chk_order_shipping_amount_positive CHECK (shipping_amount >= 0),
    CONSTRAINT chk_order_total_amount_positive CHECK (total_amount >= 0),

    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
) ENGINE=InnoDB;

--
CREATE TABLE IF NOT EXISTS order_items (
    order_id CHAR(36) NOT NULL,
    product_id CHAR(36) NOT NULL,

    product_title VARCHAR(255) NOT NULL,
    product_image VARCHAR(500) NOT NULL,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    quantity INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_order_item_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_order_item_price_positive CHECK (price > 0),

    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT uk_order_product UNIQUE (order_id, product_id),

    PRIMARY KEY (order_id, product_id) -- Added because Render need it
) ENGINE=InnoDB;
