/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * November 9, 2026
 * Class: ProductValidatorTest
 * Purpose: Tests valid and invalid inventory input, including identifiers,
 * text, categories, suppliers, quantities, and prices.
 */
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductValidatorTest {
    private final ProductValidator validator = new ProductValidator();

    // =========================================================
    // Test 1: Product ID Validation
    // =========================================================
    /**
     * Method: testProductIdValidation
     * Purpose: Accepts exactly seven digits and rejects other formats.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testProductIdValidation() {
        assertAll("Product ID validation",
                () -> assertTrue(validator.isValidProductId("1234567")),
                () -> assertTrue(validator.isValidProductId("0000001")),
                () -> assertFalse(validator.isValidProductId("123456")),
                () -> assertFalse(validator.isValidProductId("12345678")),
                () -> assertFalse(validator.isValidProductId("12345ab")),
                () -> assertFalse(validator.isValidProductId(null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> validator.validateProductId("123")));
    }

    // =========================================================
    // Test 2: Text, Category, and Supplier Validation
    // =========================================================
    /**
     * Method: testTextValidation
     * Purpose: Rejects blank product names and trims valid text.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testTextValidation() {
        assertAll("Product name validation",
                () -> assertFalse(validator.isValidText("  ")),
                () -> assertFalse(validator.isValidText(null)),
                () -> assertTrue(validator.isValidText("Laptop")),
                () -> assertEquals("Laptop", validator.validateText(" Laptop ", "Product name")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> validator.validateText("", "Product name")));
    }

    /**
     * Method: testCategoryAndSupplierValidation
     * Purpose: Requires at least one letter in category and supplier fields.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testCategoryAndSupplierValidation() {
        assertAll("Category and supplier validation",
                () -> assertTrue(validator.isValidCategory("Electronics")),
                () -> assertFalse(validator.isValidCategory("12345")),
                () -> assertFalse(validator.isValidCategory(null)),
                () -> assertTrue(validator.isValidSupplier("Tech Supply")),
                () -> assertFalse(validator.isValidSupplier("!!123")),
                () -> assertEquals("Electronics", validator.validateCategory(" Electronics ")),
                () -> assertEquals("Dell", validator.validateSupplier(" Dell ")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> validator.validateCategory("123")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> validator.validateSupplier("999")));
    }

    // =========================================================
    // Test 3: Quantity and Price Validation
    // =========================================================
    /**
     * Method: testQuantityValidation
     * Purpose: Accepts zero and positive quantities but rejects negative values.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testQuantityValidation() {
        assertAll("Quantity validation",
                () -> assertTrue(validator.isValidQuantity(0)),
                () -> assertTrue(validator.isValidQuantity(10)),
                () -> assertFalse(validator.isValidQuantity(-1)),
                () -> assertEquals(5, validator.validateQuantity(5)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> validator.validateQuantity(-5)));
    }

    /**
     * Method: testPriceValidation
     * Purpose: Accepts non-negative prices and rejects negative or missing prices.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testPriceValidation() {
        assertAll("Price validation",
                () -> assertTrue(validator.isValidPrice(BigDecimal.ZERO)),
                () -> assertTrue(validator.isValidPrice(new BigDecimal("19.99"))),
                () -> assertFalse(validator.isValidPrice(new BigDecimal("-0.01"))),
                () -> assertFalse(validator.isValidPrice(null)),
                () -> assertEquals(new BigDecimal("19.99"),
                        validator.validatePrice(new BigDecimal("19.99"))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> validator.validatePrice(new BigDecimal("-1"))));
    }
}
