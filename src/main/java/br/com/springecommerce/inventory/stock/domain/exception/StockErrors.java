package br.com.springecommerce.inventory.stock.domain.exception;

public final class StockErrors {

    private StockErrors() {}

    public final static class Target {
        public static final String STOCK = "stock";
        public final static String ID = "stock.id";
        public final static String PRODUCT_ID = "stock.product_id";
        public final static String QUANTITY = "stock.available_quantity";
        public final static String RESERVED_QUANTITY = "stock.reserved_quantity";
        public final static String MIN_QUANTITY = "stock.min_quantity";
    }

    public final static class Code {
        public static final String STOCK_NOT_FOUND = "error.stock.not_found";
        public static final String STOCK_ID_REQUIRED = "error.stock.id.required";

        public static final String STOCK_PRODUCT_REQUIRED = "error.stock.product.required";
        public static final String STOCK_PRODUCT_INVALID = "error.stock.product.invalid";
        public static final String STOCK_PRODUCT_ALREADY_EXISTS = "error.stock.product.already_exists";

        public static final String STOCK_AVAILABLE_QUANTITY_REQUIRED = "error.stock.available_quantity.required";
        public static final String STOCK_AVAILABLE_QUANTITY_INVALID = "error.stock.available_quantity.invalid";
        public static final String STOCK_QUANTITY_INSUFFICIENT = "error.stock.available_quantity.insufficient";

        public static final String STOCK_RESERVED_QUANTITY_REQUIRED = "error.stock.reserved_quantity.required";
        public static final String STOCK_RESERVED_QUANTITY_INVALID = "error.stock.reserved_quantity.invalid";
        public static final String STOCK_RESERVED_QUANTITY_EXCEEDS_STOCK = "error.stock.reserved_quantity.exceeds_stock";
        public static final String STOCK_RESERVED_QUANTITY_INSUFFICIENT = "error.stock.reserved_quantity.exceeds_stock";

        public static final String STOCK_MINIMUM_QUANTITY_REQUIRED = "error.stock.min_quantity.required";
        public static final String STOCK_MINIMUM_QUANTITY_INVALID = "error.stock.min_quantity.invalid";
        public static final String STOCK_MINIMUM_QUANTITY_EXCEEDED = "error.stock.min_quantity.exceeded";
    }
}
