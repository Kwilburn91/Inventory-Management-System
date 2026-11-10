/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * November 9, 2026
 * Class: ProductTest
 * Purpose: Tests product creation, field storage, setter validation,
 * and rejection of invalid product data.
 */
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    // =========================================================
    // Test 1: Create Data
    // =========================================================
    /**
     * Method: testCreateValidProduct
     * Purpose: Verifies that a product stores all valid constructor values.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testCreateValidProduct() {
        Product product = new Product("1234567", "Laptop", "Electronics",
                "Dell", 2, new BigDecimal("799.99"));
        assertAll("Product fields",
                () -> assertEquals("1234567", product.getProductId()),
                () -> assertEquals("Laptop", product.getProductName()),
                () -> assertEquals("Electronics", product.getCategory()),
                () -> assertEquals("Dell", product.getSupplier()),
                () -> assertEquals(2, product.getUnitQuantity()),
                () -> assertEquals(0, product.getUnitPrice().compareTo(new BigDecimal("799.99"))));
    }

    /**
     * Method: testInvalidProductCannotBeCreated
     * Purpose: Verifies that the constructor rejects invalid IDs and negative values.
     * Parameters: none.
     * Return: void.
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
     * Method: testUpdateProductFields
     * Purpose: Verifies that valid setter changes are saved to the product.
     * Parameters: none.
     * Return: void.
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
     * Method: testInvalidSetterValuesAreRejected
     * Purpose: Verifies that invalid updates throw exceptions without changing stored values.
     * Parameters: none.
     * Return: void.
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
