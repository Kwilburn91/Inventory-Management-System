package IMS;
/**
 * @author Kelly Wilburn
 * CEN 3024 - Software Development 1
 * @since November 16, 2026
 * Tests the {@link DatabaseManager} persistence layer of the Inventory
 * Management System using JUnit 5 and an isolated SQLite database.
 * Exercises database connections, product CRUD operations, duplicate
 * detection, invalid-record cleanup, and total inventory valuation.
 * {@link Product} instances provide valid test data, while a temporary
 * database keeps these tests separate from the user's inventory.
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
     * Creates a separate SQLite database and its products table before each
     * test, then connects a new {@link DatabaseManager} to that database.
     *
     * @throws SQLException if the temporary database, table, or connection
     *         cannot be created
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
     * Closes the active {@link DatabaseManager} connection after each test.
     *
     * @throws SQLException if closing the database connection fails
     */
    @AfterEach
    void tearDown() throws SQLException {
        if (manager != null) {
            manager.close();
        }
    }

    /**
     * Constructs a valid {@link Product} with standard test category and
     * supplier values and caller-specified identifier, name, quantity, and price.
     *
     * @param id the seven-digit product identifier
     * @param name the product name
     * @param quantity the non-negative number of units in stock
     * @param price the non-negative unit price in decimal-string format
     * @return a new product with the specified test data
     * @throws NumberFormatException if {@code price} is not a valid decimal value
     * @throws IllegalArgumentException if a product field fails validation
     */
    private Product createProduct(String id, String name, int quantity, String price) {
        return new Product(id, name, "Electronics", "Tech Supply", quantity,
                new BigDecimal(price));
    }

    // =========================================================
    // Test 1: Connect to Database
    // =========================================================
    /**
     * Verifies that the database manager reports an open connection and that
     * closing the connection changes the reported state to disconnected.
     *
     * @throws SQLException if the database connection cannot be closed
     */
    @Test
    void testConnectAndCloseDatabase() throws SQLException {
        assertTrue(manager.isConnected());
        manager.close();
        assertFalse(manager.isConnected());
    }

    /**
     * Verifies that attempting to connect to a blank path or a nonexistent
     * database file produces {@link SQLException}.
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
     * Verifies that an inserted product can be found by identifier and by a
     * case-insensitive name search, and is included in counts and listings.
     *
     * @throws SQLException if a database insert or query fails
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
     * Verifies that attempting to insert duplicate product identifiers or
     * case-insensitive duplicate names raises {@link SQLException}, leaving
     * the existing product count unchanged.
     *
     * @throws SQLException if the initial test product cannot be inserted
     *         or the final product count cannot be queried
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
     * Verifies that searching for an identifier absent from the database
     * returns {@code null} instead of a product.
     *
     * @throws SQLException if the product lookup fails
     */
    @Test
    void testSearchMissingProduct() throws SQLException {
        assertNull(manager.findProduct("9999999"));
    }

    // =========================================================
    // Test 3: Update and Remove Data
    // =========================================================
    /**
     * Verifies that updating a stored product changes its name, category,
     * quantity, and price while retaining access by its original identifier.
     *
     * @throws SQLException if the product insert, update, or lookup fails
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
     * Verifies that removing one product deletes its database record and
     * reduces the total product count to zero.
     *
     * @throws SQLException if the product insert, delete, or query fails
     */
    @Test
    void testDeleteSingleProduct() throws SQLException {
        manager.insertProduct(createProduct("1234567", "Keyboard", 4, "25.50"));
        assertTrue(manager.deleteProduct("1234567"));
        assertNull(manager.findProduct("1234567"));
        assertEquals(0, manager.getProductCount());
    }

    /**
     * Verifies that batch deletion removes the requested product identifiers
     * while leaving unselected products in the database.
     *
     * @throws SQLException if any product insert, deletion, or query fails
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
     * Verifies that the total inventory value for an empty database is zero.
     *
     * @throws SQLException if the inventory valuation query fails
     */
    @Test
    void testEmptyInventoryValue() throws SQLException {
        assertEquals(0, manager.getTotalInventoryValue().compareTo(BigDecimal.ZERO));
    }

    /**
     * Verifies that total inventory value sums quantity multiplied by unit
     * price across all stored products.
     *
     * @throws SQLException if test products cannot be inserted or valued
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
     * Inserts an invalid database row alongside a valid product and verifies
     * that rejected records are reported and removable without deleting the
     * valid product.
     *
     * @throws SQLException if the test row cannot be inserted or database
     *         inspection and cleanup fail
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
