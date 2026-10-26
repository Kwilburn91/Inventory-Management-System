
/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * October 26, 2026
 * IMSTest.java
 *
 * Purpose: Tests the Inventory Management System (IMS) inventory operations.
 * Verifies adding, removing, updating, and searching for products, loading
 * product data from text files, displaying inventory, and calculating the
 * total inventory value.
 */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class IMSTest {

    @TempDir
    Path tempDir;

    /**
     * Method: createTestProduct
     * Purpose: Creates a valid product for use in inventory tests.
     * Parameters: id - product ID; name - product name; quantity - quantity;
     * price - unit price.
     * Return: Product - a valid product instance.
     */
    private Product createTestProduct(
            String id,
            String name,
            int quantity,
            String price) {

        return new Product(
                id,
                name,
                "Electronics",
                "Test Supplier",
                quantity,
                new BigDecimal(price));
    }

    // =========================================================
    // Add Objects
    // =========================================================

    /**
     * Method: testAddProduct
     * Purpose: Verifies that a valid product can be added to inventory.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testAddProduct() {
        IMS inventory = new IMS();

        Product product = createTestProduct(
                "1234567", "Laptop", 5, "799.99");

        assertTrue(inventory.addProduct(product));
        assertEquals(1, inventory.getProductCount());
        assertNotNull(inventory.findProduct("1234567"));
        assertEquals(
                "Laptop",
                inventory.findProduct("1234567").getProductName());
    }

    /**
     * Method: testRejectDuplicateProductId
     * Purpose: Verifies that a product with an existing ID cannot be added.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testRejectDuplicateProductId() {
        IMS inventory = new IMS();

        Product first = createTestProduct(
                "1234567", "Laptop", 5, "799.99");

        Product duplicate = createTestProduct(
                "1234567", "Desktop", 3, "999.99");

        assertTrue(inventory.addProduct(first));
        assertFalse(inventory.addProduct(duplicate));
        assertEquals(1, inventory.getProductCount());
    }

    /**
     * Method: testRejectDuplicateProductName
     * Purpose: Verifies that duplicate product names are rejected,
     * regardless of capitalization.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testRejectDuplicateProductName() {
        IMS inventory = new IMS();

        Product first = createTestProduct(
                "1234567", "Laptop", 5, "799.99");

        Product duplicate = createTestProduct(
                "7654321", "lAPtOp", 2, "599.99");

        assertTrue(inventory.addProduct(first));
        assertFalse(inventory.addProduct(duplicate));
        assertEquals(1, inventory.getProductCount());
    }

    /**
     * Method: testRejectNullProduct
     * Purpose: Verifies that null cannot be added to inventory.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testRejectNullProduct() {
        IMS inventory = new IMS();

        assertFalse(inventory.addProduct(null));
        assertEquals(0, inventory.getProductCount());
    }

    // =========================================================
    // Remove Objects
    // =========================================================

    /**
     * Method: testRemoveProduct
     * Purpose: Verifies that an existing product can be removed.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testRemoveProduct() {
        IMS inventory = new IMS();

        Product product = createTestProduct(
                "1234567", "Laptop", 5, "799.99");

        inventory.addProduct(product);

        assertTrue(inventory.removeProduct("1234567"));
        assertEquals(0, inventory.getProductCount());
        assertNull(inventory.findProduct("1234567"));
    }

    /**
     * Method: testRemoveNonexistentProduct
     * Purpose: Verifies that removing an unknown product ID returns false.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testRemoveNonexistentProduct() {
        IMS inventory = new IMS();

        assertFalse(inventory.removeProduct("9999999"));
        assertEquals(0, inventory.getProductCount());
    }

    // =========================================================
    // Update Objects
    // =========================================================

    /**
     * Method: testModifyProduct
     * Purpose: Verifies that all modified data entries of an existing product can be updated.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testModifyProduct() {
        IMS inventory = new IMS();

        Product product = createTestProduct(
                "1234567", "Laptop", 5, "799.99");

        inventory.addProduct(product);

        boolean updated = inventory.modifyProduct(
                "1234567",
                "Gaming Laptop",
                "Computers",
                "Updated Supplier",
                10,
                new BigDecimal("999.99"));

        assertTrue(updated);

        Product updatedProduct = inventory.findProduct("1234567");

        assertNotNull(updatedProduct);
        assertEquals("Gaming Laptop", updatedProduct.getProductName());
        assertEquals("Computers", updatedProduct.getCategory());
        assertEquals("Updated Supplier", updatedProduct.getSupplier());
        assertEquals(10, updatedProduct.getUnitQuantity());
        assertEquals(
                0,
                new BigDecimal("999.99").compareTo(
                        updatedProduct.getUnitPrice()));
    }

    /**
     * Method: testModifyNonexistentProduct
     * Purpose: Verifies that an unknown product cannot be updated.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testModifyNonexistentProduct() {
        IMS inventory = new IMS();

        assertFalse(inventory.modifyProduct(
                "9999999",
                "Unknown Product",
                "Electronics",
                "Supplier",
                1,
                new BigDecimal("10.00")));
    }

    /**
     * Method: testRejectDuplicateNameDuringUpdate
     * Purpose: Verifies that an update cannot give a product the name of another product, ignores capitalization.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testRejectDuplicateNameDuringUpdate() {
        IMS inventory = new IMS();

        Product first = createTestProduct(
                "1234567", "Laptop", 5, "799.99");

        Product second = createTestProduct(
                "7654321", "Desktop", 3, "599.99");

        inventory.addProduct(first);
        inventory.addProduct(second);

        assertFalse(inventory.modifyProduct(
                "7654321",
                "laptop",
                "Electronics",
                "Test Supplier",
                3,
                new BigDecimal("599.99")));

        assertEquals(
                "Desktop",
                inventory.findProduct("7654321").getProductName());
    }

    // =========================================================
    // Search Objects
    // =========================================================

    /**
     * Method: testFindProduct
     * Purpose: Verifies that a product can be found using its ID.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testFindProduct() {
        IMS inventory = new IMS();

        Product product = createTestProduct(
                "1234567", "Laptop", 5, "799.99");

        inventory.addProduct(product);

        Product found = inventory.findProduct("1234567");

        assertNotNull(found);
        assertEquals("Laptop", found.getProductName());
    }

    /**
     * Method: testFindNonexistentProduct
     * Purpose: Verifies that searching for a missing product returns null.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testFindNonexistentProduct() {
        IMS inventory = new IMS();

        assertNull(inventory.findProduct("9999999"));
        assertNull(inventory.findProduct(null));
    }

    // =========================================================
    // Opening a File
    // Test 2: Read Data
    // =========================================================

    /**
     * Method: testLoadProductsFromTextFile
     * Purpose: Verifies that correctly formatted product records can be loaded from a text file into inventory.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testLoadProductsFromTextFile() throws Exception {
        Path file = tempDir.resolve("inventory.txt");

        Files.writeString(
                file,
                "1234567-Laptop-Electronics-Dell-5-799.99\n"
                        + "7654321-Keyboard-Accessories-Logitech-10-25.50\n",
                StandardCharsets.UTF_8);

        IMS inventory = new IMS();

        int addedCount = inventory.loadProducts(file.toString());

        assertEquals(2, addedCount);
        assertEquals(2, inventory.getProductCount());
        assertNotNull(inventory.findProduct("1234567"));
        assertNotNull(inventory.findProduct("7654321"));
        assertFalse(inventory.wasLastFileEmpty());
        assertEquals(0, inventory.getLastSkippedLineCount());
    }

    /**
     * Method: testLoadEmptyTextFile
     * Purpose: Verifies that an empty text file is handled correctly.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testLoadEmptyTextFile() throws Exception {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "", StandardCharsets.UTF_8);

        IMS inventory = new IMS();

        int addedCount = inventory.loadProducts(file.toString());

        assertEquals(0, addedCount);
        assertEquals(0, inventory.getProductCount());
        assertTrue(inventory.wasLastFileEmpty());
    }

    /**
     * Method: testLoadNonexistentFile
     * Purpose: Verifies that attempting to load a missing text file
     * returns the documented error result.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testLoadNonexistentFile() {
        IMS inventory = new IMS();

        String missingFile =
                tempDir.resolve("missing.txt").toString();

        assertEquals(-1, inventory.loadProducts(missingFile));
        assertEquals(0, inventory.getProductCount());
    }

    /**
     * Method: testRejectUnsupportedFileExtension
     * Purpose: Verifies that a file without the .txt extension is rejected.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testRejectUnsupportedFileExtension() {
        IMS inventory = new IMS();

        assertEquals(-1, inventory.loadProducts("inventory.csv"));
        assertEquals(-1, inventory.loadProducts(null));
    }

    /**
     * Method: testInvalidFileRecordsAreSkipped
     * Purpose: Verifies that malformed records are skipped while valid
     * records in the same file are imported.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testInvalidFileRecordsAreSkipped() throws Exception {
        Path file = tempDir.resolve("mixed.txt");

        Files.writeString(
                file,
                "1234567-Laptop-Electronics-Dell-5-799.99\n"
                        + "bad-record\n"
                        + "7654321-Keyboard-Accessories-Logitech-10-25.50\n",
                StandardCharsets.UTF_8);

        IMS inventory = new IMS();

        int addedCount = inventory.loadProducts(file.toString());

        assertEquals(2, addedCount);
        assertEquals(2, inventory.getProductCount());
        assertEquals(1, inventory.getLastSkippedLineCount());
    }

    // =========================================================
    // Test 3: Display Data
    // =========================================================

    /**
     * Method: testDisplayProducts
     * Purpose: Verifies that the inventory display includes product
     * information for existing items.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testDisplayProducts() {
        IMS inventory = new IMS();

        inventory.addProduct(createTestProduct(
                "1234567", "Laptop", 5, "799.99"));

        String display = inventory.getProductsDisplay();

        assertTrue(display.contains("Laptop"));
        assertTrue(display.contains("799.99"));
        assertTrue(display.contains("Electronics"));
        assertTrue(display.contains("Test Supplier"));
        assertTrue(display.contains("1234567"));
    }

    /**
     * Method: testDisplayEmptyInventory
     * Purpose: Verifies that an empty inventory produces an appropriate message.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testDisplayEmptyInventory() {
        IMS inventory = new IMS();

        assertTrue(inventory.getProductsDisplay().contains(
                "There are currently no items in inventory."));
    }

    // =========================================================
    // Test 7: Display Total Inventory Value
    // =========================================================

    /**
     * Method: testDisplayTotalInventoryValue
     * Purpose: Verifies that the total inventory value is calculated
     * from each product's quantity multiplied by its unit price.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testDisplayTotalInventoryValue() {
        IMS inventory = new IMS();

        inventory.addProduct(createTestProduct(
                "1234567", "Laptop", 2, "100.00"));

        inventory.addProduct(createTestProduct(
                "7654321", "Keyboard", 4, "25.50"));

        BigDecimal total = inventory.getTotalInventoryValue();

        // $200.00 for laptops plus $102.00 for keyboards.
        assertEquals(
                0,
                new BigDecimal("302.00").compareTo(total));
    }

    /**
     * Method: testTotalInventoryValueForEmptyInventory
     * Purpose: Verifies that an empty inventory has a total value of zero.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testTotalInventoryValueForEmptyInventory() {
        IMS inventory = new IMS();

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        inventory.getTotalInventoryValue()));
    }

    /**
     * Method: testTotalInventoryValueAfterRemovingProduct
     * Purpose: Verifies that removing a product updates the calculated
     * inventory value.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testTotalInventoryValueAfterRemovingProduct() {
        IMS inventory = new IMS();

        inventory.addProduct(createTestProduct(
                "1234567", "Laptop", 2, "100.00"));

        inventory.addProduct(createTestProduct(
                "7654321", "Keyboard", 4, "25.00"));

        assertTrue(inventory.removeProduct("1234567"));

        assertEquals(
                0,
                new BigDecimal("100.00").compareTo(
                        inventory.getTotalInventoryValue()));
    }
}