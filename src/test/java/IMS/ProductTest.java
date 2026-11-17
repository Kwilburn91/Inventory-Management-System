package IMS;
/**
 * @author Kelly Wilburn
 * CEN 3024 - Software Development 1
 * @since November 16, 2026
 * Tests the {@link Product} model used by the Inventory Management System.
 * Uses JUnit 5 to verify constructor initialization, field updates, and
 * validation of product identifiers, names, quantities, and prices.
 * These tests exercise the rules enforced by the Product model and its
 * validation dependencies without connecting to a database.
 */
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
class ProductTest {
    // =========================================================
    // Test 1: Create Data
    // =========================================================
    /**
     * Verifies that constructing a valid {@link Product} preserves its
     * identifier, name, category, supplier, quantity, and monetary price.
     */
    @Test
    void testCreateValidProduct() {
        Product product = new Product("1234567", "Laptop", "Electronics",
                "Dell", 2, new BigDecimal("799.99"));

        assertAll("IMS.Product fields",

                () -> assertEquals("1234567", product.getProductId()),
                () -> assertEquals("Laptop", product.getProductName()),
                () -> assertEquals("Electronics", product.getCategory()),
                () -> assertEquals("Dell", product.getSupplier()),
                () -> assertEquals(2, product.getUnitQuantity()),
                () -> assertEquals(0, product.getUnitPrice().compareTo(new BigDecimal("799.99"))));
    }

    /**
     * Verifies that the {@link Product} constructor rejects malformed product
     * identifiers, negative quantities, and negative prices by throwing
     * {@link IllegalArgumentException}.
     */
    @Test
    void testInvalidProductCannotBeCreated() {
        assertAll("Invalid constructor inputs",
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Product("123", "Laptop", "Electronics", "Dell", 2,
                                new BigDecimal("799.99"))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Product("1234567", "Laptop", "Electronics", "Dell", -1,
                                new BigDecimal("799.99"))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Product("1234567", "Laptop", "Electronics", "Dell", 2,
                                new BigDecimal("-1.00"))));
    }

    // =========================================================
    // Test 2: Update Data
    // =========================================================
    /**
     * Verifies that valid setter calls update a product's name, category,
     * supplier, quantity, and price, including a quantity of zero.
     */
    @Test
    void testUpdateProductFields() {
        Product product = new Product("1234567", "Laptop", "Electronics",
                "Dell", 2, new BigDecimal("799.99"));
        product.setProductName("Monitor");
        product.setCategory("Accessories");
        product.setSupplier("Tech Supply");
        product.setUnitQuantity(0);
        product.setUnitPrice(new BigDecimal("125.50"));
        assertAll("Updated product",
                () -> assertEquals("Monitor", product.getProductName()),
                () -> assertEquals("Accessories", product.getCategory()),
                () -> assertEquals("Tech Supply", product.getSupplier()),
                () -> assertEquals(0, product.getUnitQuantity()),
                () -> assertEquals(0, product.getUnitPrice().compareTo(new BigDecimal("125.50"))));
    }

    /**
     * Verifies that invalid product updates throw
     * {@link IllegalArgumentException} without overwriting the stored
     * quantity, identifier, or name.
     */
    @Test
    void testInvalidSetterValuesAreRejected() {
        Product product = new Product("1234567", "Laptop", "Electronics",
                "Dell", 2, new BigDecimal("799.99"));
        assertThrows(IllegalArgumentException.class, () -> product.setUnitQuantity(-3));
        assertThrows(IllegalArgumentException.class, () -> product.setProductId("123"));
        assertThrows(IllegalArgumentException.class, () -> product.setProductName(" "));
        assertEquals(2, product.getUnitQuantity());
        assertEquals("1234567", product.getProductId());
        assertEquals("Laptop", product.getProductName());
    }
}
