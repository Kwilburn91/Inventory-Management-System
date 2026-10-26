/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * October 25, 2026
 * ProductValidatorTest.java
 * This class tests ProductValidator to ensure that inventory data meets
 * the application's requirements for product IDs, text fields, categories,
 * suppliers, quantities, and prices.
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductValidatorTest {

    private ProductValidator validator;

    /**
     * Method: setUp
     * Purpose: Creates a validator before each test.
     * Parameters: none
     * Return: void
     */
    @BeforeEach
    void setUp() {
        validator = new ProductValidator();
    }

    /**
     * Method: testValidProductId
     * Purpose: Verifies that a seven-digit product ID is accepted.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Accept a valid seven-digit product ID")
    void testValidProductId() {

        assertTrue(validator.isValidProductId("1234567"));
    }

    /**
     * Method: testInvalidProductIds
     * Purpose: Verifies that malformed, blank, and null IDs are rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject invalid product IDs")
    void testInvalidProductIds() {

        assertFalse(validator.isValidProductId("123456"));
        assertFalse(validator.isValidProductId("12345678"));
        assertFalse(validator.isValidProductId("1234abc"));
        assertFalse(validator.isValidProductId(""));
        assertFalse(validator.isValidProductId(null));
    }

    /**
     * Method: testValidText
     * Purpose: Verifies that nonblank text is accepted.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Accept non-empty text")
    void testValidText() {

        assertTrue(validator.isValidText("Apple"));
        assertTrue(validator.isValidText("Green Apple"));
    }

    /**
     * Method: testInvalidText
     * Purpose: Verifies that null, empty, and whitespace-only text is rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject null or blank text")
    void testInvalidText() {

        assertFalse(validator.isValidText(null));
        assertFalse(validator.isValidText(""));
        assertFalse(validator.isValidText("   "));
    }

    /**
     * Method: testValidCategory
     * Purpose: Verifies that categories containing at least one letter are accepted.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Accept a category containing letters")
    void testValidCategory() {

        assertTrue(validator.isValidCategory("Fruit"));
        assertTrue(validator.isValidCategory("Fresh Produce"));
        assertTrue(validator.isValidCategory("Fruit123"));
    }

    /**
     * Method: testInvalidCategory
     * Purpose: Verifies that categories without letters are rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject categories without letters")
    void testInvalidCategory() {

        assertFalse(validator.isValidCategory(null));
        assertFalse(validator.isValidCategory(""));
        assertFalse(validator.isValidCategory("12345"));
        assertFalse(validator.isValidCategory("!@#$"));
    }

    /**
     * Method: testValidSupplier
     * Purpose: Verifies that supplier names containing letters are accepted.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Accept a supplier containing letters")
    void testValidSupplier() {

        assertTrue(validator.isValidSupplier("Supplier A"));
        assertTrue(validator.isValidSupplier("Company123"));
    }

    /**
     * Method: testInvalidSupplier
     * Purpose: Verifies that suppliers without letters are rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject suppliers without letters")
    void testInvalidSupplier() {

        assertFalse(validator.isValidSupplier(null));
        assertFalse(validator.isValidSupplier(""));
        assertFalse(validator.isValidSupplier("12345"));
        assertFalse(validator.isValidSupplier("!@#$"));
    }

    /**
     * Method: testValidQuantity
     * Purpose: Verifies that zero and positive quantities are accepted.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Accept non-negative quantities")
    void testValidQuantity() {

        assertTrue(validator.isValidQuantity(0));
        assertTrue(validator.isValidQuantity(10));
    }

    /**
     * Method: testInvalidQuantity
     * Purpose: Verifies that negative quantities are rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject negative quantities")
    void testInvalidQuantity() {

        assertFalse(validator.isValidQuantity(-1));
    }

    /**
     * Method: testValidPrice
     * Purpose: Verifies that zero and positive prices are accepted.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Accept non-negative prices")
    void testValidPrice() {

        assertTrue(validator.isValidPrice(BigDecimal.ZERO));
        assertTrue(validator.isValidPrice(new BigDecimal("1.50")));
        assertTrue(validator.isValidPrice(new BigDecimal("100.00")));
    }

    /**
     * Method: testInvalidPrice
     * Purpose: Verifies that negative and null prices are rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject negative or null prices")
    void testInvalidPrice() {

        assertFalse(validator.isValidPrice(new BigDecimal("-1.00")));
        assertFalse(validator.isValidPrice(null));
    }

    /**
     * Method: testValidateProductId
     * Purpose: Verifies that valid IDs are returned and invalid IDs
     * cause an IllegalArgumentException.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Validate and return a product ID")
    void testValidateProductId() {

        assertEquals(
                "1234567",
                validator.validateProductId("1234567"));

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateProductId("123"));
    }

    /**
     * Method: testValidateText
     * Purpose: Verifies that valid text is trimmed and blank text is rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Validate and trim text")
    void testValidateText() {

        assertEquals(
                "Apple",
                validator.validateText(" Apple ", "Product name"));

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateText(" ", "Product name"));
    }

    /**
     * Method: testValidateCategory
     * Purpose: Verifies that valid categories are trimmed and invalid
     * categories cause an IllegalArgumentException.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Validate and trim category")
    void testValidateCategory() {

        assertEquals(
                "Fruit",
                validator.validateCategory(" Fruit "));

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateCategory("12345"));
    }

    /**
     * Method: testValidateSupplier
     * Purpose: Verifies that valid suppliers are trimmed and invalid
     * suppliers cause an IllegalArgumentException.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Validate and trim supplier")
    void testValidateSupplier() {

        assertEquals(
                "Supplier A",
                validator.validateSupplier(" Supplier A "));

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateSupplier("12345"));
    }

    /**
     * Method: testValidateQuantityThrowsException
     * Purpose: Verifies that negative quantities cause an exception.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject negative quantity during validation")
    void testValidateQuantityThrowsException() {

        assertEquals(10, validator.validateQuantity(10));

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateQuantity(-1));
    }

    /**
     * Method: testValidatePriceThrowsException
     * Purpose: Verifies that negative or null prices cause an exception.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject negative price during validation")
    void testValidatePriceThrowsException() {

        assertEquals(
                0,
                new BigDecimal("1.50").compareTo(
                        validator.validatePrice(new BigDecimal("1.50"))));

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validatePrice(new BigDecimal("-1.00")));

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validatePrice(null));
    }
}