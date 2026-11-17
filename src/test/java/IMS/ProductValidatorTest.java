package IMS;
/**
 * @author Kelly Wilburn
 * CEN 3024 - Software Development 1
 * @since November 16, 2026
 * Tests the {@link ProductValidator} rules used to validate inventory data.
 * Uses JUnit 5 to check identifiers, required text, categories, suppliers,
 * quantities, and prices, including normalization and rejected values.
 * This suite verifies the validation behavior relied upon by product entry
 * and updates in the Inventory Management System.
 */
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductValidatorTest {
    private final ProductValidator validator = new ProductValidator();

    // =========================================================
    // Test 1: IMS.Product ID Validation
    // =========================================================
    /**
     * Verifies that a product identifier must contain exactly seven digits,
     * including identifiers with leading zeroes, and that invalid identifiers
     * are rejected by both checking and validating methods.
     */
    @Test
    void testProductIdValidation() {

        assertAll("IMS.Product ID validation",

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
     * Verifies that required text cannot be null or blank, that valid text
     * is accepted, and that validation trims surrounding whitespace.
     */
    @Test
    void testTextValidation() {

        assertAll("IMS.Product name validation",

                () -> assertFalse(validator.isValidText("  ")),
                () -> assertFalse(validator.isValidText(null)),
                () -> assertTrue(validator.isValidText("Laptop")),

                () -> assertEquals("Laptop", validator.validateText(" Laptop ", "IMS.Product name")),

                () -> assertThrows(IllegalArgumentException.class,

                        () -> validator.validateText("", "IMS.Product name")));

    }

    /**
     * Verifies that category and supplier values contain at least one letter,
     * that valid entries are trimmed, and that invalid entries are rejected.
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
     * Verifies that zero and positive integer quantities are valid and that
     * a negative quantity is rejected with {@link IllegalArgumentException}.
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
     * Verifies that zero and positive {@link java.math.BigDecimal} prices are
     * valid, while negative or null prices are rejected.
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
