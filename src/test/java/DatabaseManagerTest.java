/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * November 9, 2026
 * Class: DatabaseManagerTest
 * Purpose: Tests SQLite connection, database validation, product creation,
 * searching, updating, removal, rejected records, and inventory valuation.
 * Uses a temporary database to protect the user's inventory data.
 */
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {
    @TempDir
    Path tempDir;
    private DatabaseManager manager;
    private Path databasePath;

    /**
     * Method: setUp
     * Purpose: Creates a separate SQLite database and required products table before each test.
     * Parameters: none.
     * Return: void.
     */
    @BeforeEach
    void setUp() throws SQLException {
        databasePath = tempDir.resolve("inventory_test.db");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE products (" +
                    "product_id TEXT, product_name TEXT, category TEXT, " +
                    "supplier TEXT, unit_quantity INTEGER, unit_price TEXT)");
        }
        manager = new DatabaseManager();
        manager.connect(databasePath.toString());
    }

    /**
     * Method: tearDown
     * Purpose: Closes the database connection after each test.
     * Parameters: none.
     * Return: void.
     */
    @AfterEach
    void tearDown() throws SQLException {
        if (manager != null) {
            manager.close();
        }
    }

    /**
     * Method: createProduct
     * Purpose: Creates a valid product with a unique ID and specified price and quantity.
     * Parameters: id - product identifier; name - product name; quantity - units;
     * price - price represented as decimal text.
     * Return: Product - the new product.
     */
    private Product createProduct(String id, String name, int quantity, String price) {
        return new Product(id, name, "Electronics", "Tech Supply", quantity,
                new BigDecimal(price));
    }

    // =========================================================
    // Test 1: Connect to Database
    // =========================================================
    /**
     * Method: testConnectAndCloseDatabase
     * Purpose: Verifies that the manager connects and closes successfully.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testConnectAndCloseDatabase() throws SQLException {
        assertTrue(manager.isConnected());
        manager.close();
        assertFalse(manager.isConnected());
    }

    /**
     * Method: testInvalidDatabasePathIsRejected
     * Purpose: Verifies that missing or blank database paths are rejected.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testInvalidDatabasePathIsRejected() {
        assertAll("Invalid database paths",
                () -> assertThrows(SQLException.class, () -> manager.connect("")),
                () -> assertThrows(SQLException.class,
                        () -> manager.connect(tempDir.resolve("missing.db").toString())));
    }

    // =========================================================
    // Test 2: Create and Read Data
    // =========================================================
    /**
     * Method: testInsertAndFindProduct
     * Purpose: Verifies that an inserted product can be found by ID and name.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testInsertAndFindProduct() throws SQLException {
        Product product = createProduct("1234567", "Keyboard", 4, "25.50");
        assertTrue(manager.insertProduct(product));
        assertAll("Inserted product",
                () -> assertEquals(1, manager.getProductCount()),
                () -> assertEquals("Keyboard", manager.findProduct("1234567").getProductName()),
                () -> assertEquals("1234567", manager.findProductByName("keyboard").getProductId()),
                () -> assertEquals(1, manager.getAllProducts().size()));
    }

    /**
     * Method: testDuplicateProductIsRejected
     * Purpose: Verifies that duplicate product IDs and names cannot be inserted.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testDuplicateProductIsRejected() throws SQLException {
        manager.insertProduct(createProduct("1234567", "Keyboard", 4, "25.50"));
        assertAll("Duplicate checks",
                () -> assertThrows(SQLException.class,
                        () -> manager.insertProduct(createProduct("1234567", "Mouse", 2, "10.00"))),
                () -> assertThrows(SQLException.class,
                        () -> manager.insertProduct(createProduct("2345678", "KEYBOARD", 2, "10.00"))));
        assertEquals(1, manager.getProductCount());
    }

    /**
     * Method: testSearchMissingProduct
     * Purpose: Verifies that searching for an unknown ID returns null.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testSearchMissingProduct() throws SQLException {
        assertNull(manager.findProduct("9999999"));
    }

    // =========================================================
    // Test 3: Update and Remove Data
    // =========================================================
    /**
     * Method: testUpdateProduct
     * Purpose: Verifies that a saved product's details can be updated.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testUpdateProduct() throws SQLException {
        manager.insertProduct(createProduct("1234567", "Keyboard", 4, "25.50"));
        assertTrue(manager.updateProduct("1234567", "Mechanical Keyboard",
                "Accessories", "Tech Supply", 3, new BigDecimal("50.00")));
        Product updated = manager.findProduct("1234567");
        assertAll("Updated product",
                () -> assertEquals("Mechanical Keyboard", updated.getProductName()),
                () -> assertEquals("Accessories", updated.getCategory()),
                () -> assertEquals(3, updated.getUnitQuantity()),
                () -> assertEquals(0, updated.getUnitPrice().compareTo(new BigDecimal("50.00"))));
    }

    /**
     * Method: testDeleteSingleProduct
     * Purpose: Verifies that deleting one product removes it from the database.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testDeleteSingleProduct() throws SQLException {
        manager.insertProduct(createProduct("1234567", "Keyboard", 4, "25.50"));
        assertTrue(manager.deleteProduct("1234567"));
        assertNull(manager.findProduct("1234567"));
        assertEquals(0, manager.getProductCount());
    }

    /**
     * Method: testDeleteMultipleProducts
     * Purpose: Verifies that multiple selected product IDs are removed together.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testDeleteMultipleProducts() throws SQLException {
        manager.insertProduct(createProduct("1234567", "Keyboard", 4, "25.50"));
        manager.insertProduct(createProduct("2345678", "Mouse", 2, "10.00"));
        manager.insertProduct(createProduct("3456789", "Monitor", 1, "100.00"));
        assertEquals(2, manager.deleteProducts(Arrays.asList("1234567", "2345678")));
        assertNull(manager.findProduct("1234567"));
        assertNull(manager.findProduct("2345678"));
        assertNotNull(manager.findProduct("3456789"));
        assertEquals(1, manager.getProductCount());
    }

    // =========================================================
    // Test 4: Display Total Inventory Value
    // =========================================================
    /**
     * Method: testEmptyInventoryValue
     * Purpose: Verifies that an empty inventory has zero total value.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testEmptyInventoryValue() throws SQLException {
        assertEquals(0, manager.getTotalInventoryValue().compareTo(BigDecimal.ZERO));
    }

    /**
     * Method: testCalculatedInventoryValue
     * Purpose: Verifies that total value equals the sum of quantity multiplied by price.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testCalculatedInventoryValue() throws SQLException {
        manager.insertProduct(createProduct("1234567", "Keyboard", 4, "25.50"));
        manager.insertProduct(createProduct("2345678", "Mouse", 2, "10.00"));
        assertEquals(0, manager.getTotalInventoryValue().compareTo(new BigDecimal("122.00")));
    }

    // =========================================================
    // Test 5: Reject Invalid Database Records
    // =========================================================
    /**
     * Method: testInvalidRecordsCanBeIdentifiedAndRemoved
     * Purpose: Verifies that invalid rows are reported and can be deleted without
     * removing valid inventory records.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testInvalidRecordsCanBeIdentifiedAndRemoved() throws SQLException {
        manager.insertProduct(createProduct("1234567", "Keyboard", 4, "25.50"));
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO products VALUES " +
                    "('12', 'Invalid Item', 'Electronics', 'Supplier', -2, '5.00')");
        }
        assertEquals(1, manager.getAllProducts().size());
        List<String> rejected = manager.getRejectedRecords();
        assertEquals(1, rejected.size());
        assertTrue(rejected.get(0).contains("12"));
        assertEquals(1, manager.deleteRejectedRecords());
        assertEquals(1, manager.getProductCount());
        assertEquals(1, manager.getAllProducts().size());
        assertTrue(manager.getRejectedRecords().isEmpty());
    }
}
