/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * October 25, 2026
 * ProductTest.java
 * This class tests the Product model to verify that product information
 * is validated, stored, retrieved, and updated correctly for the
 * Inventory Management System.
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    /**
     * Method: setUp
     * Purpose: Creates a valid product before each test.
     * Parameters: none
     * Return: void
     */
    @BeforeEach
    void setUp() {

        product = new Product(
                "1234567",
                "Laptop",
                "Electronics",
                "FoodMart",
                5,
                new BigDecimal("850.00"));
    }

    /**
     * Method: testProductConstructor
     * Purpose: Verifies that a Product object stores its initial values.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Create a product successfully")
    void testProductConstructor() {

        assertNotNull(product);
        assertEquals("1234567", product.getProductId());
        assertEquals("Laptop", product.getProductName());
        assertEquals("Electronics", product.getCategory());
        assertEquals("FoodMart", product.getSupplier());
        assertEquals(5, product.getUnitQuantity());

        assertEquals(
                0,
                new BigDecimal("850.00").compareTo(product.getUnitPrice()));
    }

    /**
     * Method: testInvalidProductId
     * Purpose: Verifies that an invalid product ID prevents construction.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject an invalid product ID")
    void testInvalidProductId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(
                        "123",
                        "Apple",
                        "Fruit",
                        "Supplier A",
                        10,
                        new BigDecimal("1.50")));
    }

    /**
     * Method: testInvalidProductName
     * Purpose: Verifies that an empty product name prevents construction.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject an empty product name")
    void testInvalidProductName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(
                        "1234567",
                        " ",
                        "Fruit",
                        "Supplier A",
                        10,
                        new BigDecimal("1.50")));
    }

    /**
     * Method: testInvalidCategory
     * Purpose: Verifies that a category without letters prevents construction.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject an invalid category")
    void testInvalidCategory() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(
                        "1234567",
                        "Apple",
                        "12345",
                        "Supplier A",
                        10,
                        new BigDecimal("1.50")));
    }

    /**
     * Method: testInvalidSupplier
     * Purpose: Verifies that a supplier without letters prevents construction.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject an invalid supplier")
    void testInvalidSupplier() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(
                        "1234567",
                        "Apple",
                        "Fruit",
                        "12345",
                        10,
                        new BigDecimal("1.50")));
    }

    /**
     * Method: testInvalidQuantity
     * Purpose: Verifies that a negative quantity prevents construction.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject a negative quantity")
    void testInvalidQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(
                        "1234567",
                        "Apple",
                        "Fruit",
                        "Supplier A",
                        -1,
                        new BigDecimal("1.50")));
    }

    /**
     * Method: testInvalidPrice
     * Purpose: Verifies that a negative price prevents construction.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject a negative price")
    void testInvalidPrice() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(
                        "1234567",
                        "Apple",
                        "Fruit",
                        "Supplier A",
                        10,
                        new BigDecimal("-1.50")));
    }

    /**
     * Method: testSetProductId
     * Purpose: Verifies that the product ID can be updated.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Update product ID")
    void testSetProductId() {

        product.setProductId("7654321");

        assertEquals("7654321", product.getProductId());
    }

    /**
     * Method: testSetInvalidProductId
     * Purpose: Verifies that an invalid replacement ID is rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject an invalid replacement product ID")
    void testSetInvalidProductId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> product.setProductId("123"));
    }

    /**
     * Method: testSetProductName
     * Purpose: Verifies that the product name can be updated.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Update product name")
    void testSetProductName() {

        product.setProductName("Green Apple");

        assertEquals("Green Apple", product.getProductName());
    }

    /**
     * Method: testSetInvalidProductName
     * Purpose: Verifies that an empty replacement name is rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject an empty replacement product name")
    void testSetInvalidProductName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> product.setProductName(" "));
    }

    /**
     * Method: testSetCategory
     * Purpose: Verifies that the category can be updated.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Update category")
    void testSetCategory() {

        product.setCategory("Produce");

        assertEquals("Produce", product.getCategory());
    }

    /**
     * Method: testSetInvalidCategory
     * Purpose: Verifies that an invalid replacement category is rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject an invalid replacement category")
    void testSetInvalidCategory() {

        assertThrows(
                IllegalArgumentException.class,
                () -> product.setCategory("12345"));
    }

    /**
     * Method: testSetSupplier
     * Purpose: Verifies that the supplier can be updated.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Update supplier")
    void testSetSupplier() {

        product.setSupplier("Supplier B");

        assertEquals("Supplier B", product.getSupplier());
    }

    /**
     * Method: testSetInvalidSupplier
     * Purpose: Verifies that an invalid replacement supplier is rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject an invalid replacement supplier")
    void testSetInvalidSupplier() {

        assertThrows(
                IllegalArgumentException.class,
                () -> product.setSupplier("12345"));
    }

    /**
     * Method: testSetUnitQuantity
     * Purpose: Verifies that the quantity can be updated.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Update quantity")
    void testSetUnitQuantity() {

        product.setUnitQuantity(25);

        assertEquals(25, product.getUnitQuantity());
    }

    /**
     * Method: testSetInvalidUnitQuantity
     * Purpose: Verifies that a negative replacement quantity is rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject a negative replacement quantity")
    void testSetInvalidUnitQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> product.setUnitQuantity(-1));
    }

    /**
     * Method: testSetUnitPrice
     * Purpose: Verifies that the unit price can be updated.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Update unit price")
    void testSetUnitPrice() {

        product.setUnitPrice(new BigDecimal("2.75"));

        assertEquals(
                0,
                new BigDecimal("2.75").compareTo(product.getUnitPrice()));
    }

    /**
     * Method: testSetInvalidUnitPrice
     * Purpose: Verifies that a negative replacement price is rejected.
     * Parameters: none
     * Return: void
     */
    @Test
    @DisplayName("Reject a negative replacement price")
    void testSetInvalidUnitPrice() {

        assertThrows(
                IllegalArgumentException.class,
                () -> product.setUnitPrice(new BigDecimal("-2.00")));
    }
}