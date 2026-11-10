
/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * November 8, 2026
 * Class: DatabaseManager
 * Purpose: Connects the Inventory Management System to a user-selected SQLite
 * database. It retrieves, inserts, updates, deletes, and calculates inventory
 * records directly in the database using prepared SQL statements.
 */

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements AutoCloseable {

    private Connection connection;
    private final List<String> rejectedRecords = new ArrayList<>();
    private final List<Long> rejectedRowIds = new ArrayList<>();

    /**
     * Method: connect
     * Purpose: Opens the SQLite database selected by the user and verifies that
     * it contains the inventory table and required columns.
     * Parameters: databasePath - user-supplied path to the SQLite database file.
     * Return: void.
     */
    public void connect(String databasePath) throws SQLException {
        if (databasePath == null || databasePath.trim().isEmpty()) {
            throw new SQLException("Database path cannot be empty.");
        }

        close();
        File databaseFile = new File(databasePath.trim()).getAbsoluteFile();
        if (!databaseFile.exists() || !databaseFile.isFile()) {
            throw new SQLException("The selected database file could not be found.");
        }

        String normalizedPath = databaseFile.getAbsolutePath()
                .replace(File.separatorChar, '/');
        String connectionUrl = "jdbc:sqlite:" + normalizedPath;
        Connection newConnection = DriverManager.getConnection(connectionUrl);
        newConnection.setAutoCommit(true);
        try (java.sql.Statement setupStatement = newConnection.createStatement()) {
            setupStatement.execute("PRAGMA busy_timeout = 5000");
        }

        try {
            verifyDatabaseSchema(newConnection);
        } catch (SQLException exception) {
            newConnection.close();
            throw exception;
        }

        connection = newConnection;
    }

    /**
     * Method: verifyDatabaseSchema
     * Purpose: Confirms that the selected database contains the products table
     * with the six columns required by the Inventory Management System.
     * Parameters: candidate - the database connection being checked.
     * Return: void.
     */
    private void verifyDatabaseSchema(Connection candidate) throws SQLException {
        String sql = "SELECT product_id, product_name, category, supplier, "
                + "unit_quantity, unit_price FROM products LIMIT 0";
        try (PreparedStatement statement = candidate.prepareStatement(sql)) {
            statement.executeQuery().close();
        } catch (SQLException exception) {
            throw new SQLException(
                    "This is not a compatible IMS database.\n Please select a different file.",
                    exception);
        }
    }

    /**
     * Method: isConnected
     * Purpose: Reports whether a usable SQLite database connection is open.
     * Parameters: none.
     * Return: boolean - true when connected; otherwise false.
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException exception) {
            return false;
        }
    }

    /**
     * Method: getAllProducts
     * Purpose: Retrieves every product record from SQLite in alphabetical order.
     * Parameters: none.
     * Return: List<Product> - the database product records.
     */
    public List<Product> getAllProducts() throws SQLException {
        ensureConnected();
        List<Product> products = new ArrayList<>();
        rejectedRecords.clear();
        rejectedRowIds.clear();
        String sql = "SELECT rowid AS ims_rowid, product_id, product_name, category, supplier, "
                + "unit_quantity, unit_price FROM products "
                + "ORDER BY product_name COLLATE NOCASE";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                try {
                    products.add(readProduct(results));
                } catch (SQLException | IllegalArgumentException exception) {
                    String id = results.getString("product_id");
                    rejectedRecords.add("Product ID: " + (id == null ? "(missing)" : id)
                            + " — " + exception.getMessage());
                    rejectedRowIds.add(results.getLong("ims_rowid"));
                }
            }
        }
        return products;
    }

    /**
     * Method: getRejectedRecords
     * Purpose: Returns descriptions of invalid records found during the most
     * recent getAllProducts query without changing the database.
     * Parameters: none.
     * Return: List<String> - rejected record IDs and validation errors.
     */
    public List<String> getRejectedRecords() {
        return new ArrayList<>(rejectedRecords);
    }

    /**
     * Method: deleteRejectedRecords
     * Purpose: Removes only the invalid rows found in the most recent inventory
     * scan, using SQLite row IDs so malformed or duplicate product IDs are safe.
     * All deletions occur in one transaction and are rolled back on failure.
     * Parameters: none.
     * Return: int - number of invalid records deleted.
     */
    public int deleteRejectedRecords() throws SQLException {
        ensureConnected();
        if (rejectedRowIds.isEmpty()) {
            return 0;
        }
        int removed = 0;
        connection.setAutoCommit(false);
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM products WHERE rowid = ?")) {
            for (Long rowId : rejectedRowIds) {
                statement.setLong(1, rowId);
                removed += statement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException exception) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                exception.addSuppressed(rollbackException);
            }
            throw exception;
        } finally {
            connection.setAutoCommit(true);
        }
        rejectedRecords.clear();
        rejectedRowIds.clear();
        return removed;
    }

    /**
     * Method: findProduct
     * Purpose: Retrieves one product from SQLite using its seven-digit ID.
     * Parameters: productId - the product ID being searched for.
     * Return: Product - the matching product, or null when it is not found.
     */
    public Product findProduct(String productId) throws SQLException {
        ensureConnected();
        String sql = "SELECT product_id, product_name, category, supplier, "
                + "unit_quantity, unit_price FROM products WHERE product_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? readProduct(results) : null;
            }
        }
    }

    /**
     * Method: findProductByName
     * Purpose: Finds a product name without regard to capitalization so that
     * duplicate product names can be prevented before a database write.
     * Parameters: productName - the product name being checked.
     * Return: Product - the matching product, or null when it is not found.
     */
    public Product findProductByName(String productName) throws SQLException {
        ensureConnected();
        String sql = "SELECT product_id, product_name, category, supplier, "
                + "unit_quantity, unit_price FROM products "
                + "WHERE product_name = ? COLLATE NOCASE";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productName.trim());
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? readProduct(results) : null;
            }
        }
    }

    /**
     * Method: insertProduct
     * Purpose: Inserts a validated product directly into the connected SQLite database.
     * Parameters: product - the product record to save.
     * Return: boolean - true when one record is inserted; otherwise false.
     */
    public boolean insertProduct(Product product) throws SQLException {
        ensureConnected();
        if (product == null) {
            throw new SQLException("Product information is missing.");
        }

        if (findProduct(product.getProductId()) != null) {
            throw new SQLException("That product ID already exists in the database.");
        }
        if (findProductByName(product.getProductName()) != null) {
            throw new SQLException("That product name already exists in the database.");
        }

        String sql = "INSERT INTO products (product_id, product_name, category, "
                + "supplier, unit_quantity, unit_price) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getProductId());
            statement.setString(2, product.getProductName());
            statement.setString(3, product.getCategory());
            statement.setString(4, product.getSupplier());
            statement.setInt(5, product.getUnitQuantity());
            statement.setString(6, product.getUnitPrice().setScale(2, RoundingMode.HALF_UP).toPlainString());
            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Method: updateProduct
     * Purpose: Updates the selected product record directly in SQLite while
     * preventing the new product name from duplicating another record.
     * Parameters: productId, productName, category, supplier, unitQuantity, unitPrice.
     * Return: boolean - true when one record is updated; otherwise false.
     */
    public boolean updateProduct(String productId, String productName,
                                 String category, String supplier,
                                 int unitQuantity, BigDecimal unitPrice)
            throws SQLException {
        ensureConnected();
        Product duplicateName = findProductByName(productName);
        if (duplicateName != null && !duplicateName.getProductId().equals(productId)) {
            throw new SQLException("That product name already exists in the database.");
        }

        String sql = "UPDATE products SET product_name = ?, category = ?, supplier = ?, "
                + "unit_quantity = ?, unit_price = ? WHERE product_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productName.trim());
            statement.setString(2, category.trim());
            statement.setString(3, supplier.trim());
            statement.setInt(4, unitQuantity);
            statement.setString(5, unitPrice.setScale(2).toPlainString());
            statement.setString(6, productId);
            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Method: deleteProduct
     * Purpose: Deletes one product record from the connected SQLite database.
     * Parameters: productId - the ID of the product to delete.
     * Return: boolean - true when one record is deleted; otherwise false.
     */
    public boolean deleteProduct(String productId) throws SQLException {
        ensureConnected();
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Method: deleteProducts
     * Purpose: Deletes a group of selected product records as one transaction,
     * rolling back the entire operation if a database error occurs.
     * Parameters: productIds - IDs selected for removal.
     * Return: int - the number of records removed.
     */
    public int deleteProducts(List<String> productIds) throws SQLException {
        ensureConnected();
        if (productIds == null || productIds.isEmpty()) {
            return 0;
        }
        String sql = "DELETE FROM products WHERE product_id = ?";
        int removed = 0;
        connection.setAutoCommit(false);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String productId : productIds) {
                statement.setString(1, productId);
                removed += statement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException exception) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                exception.addSuppressed(rollbackException);
            }
            throw exception;
        } finally {
            connection.setAutoCommit(true);
        }
        return removed;
    }

    /**
     * Method: getProductCount
     * Purpose: Retrieves the number of product records currently stored in SQLite.
     * Parameters: none.
     * Return: int - the number of database records.
     */
    public int getProductCount() throws SQLException {
        ensureConnected();
        String sql = "SELECT COUNT(*) FROM products";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            return results.next() ? results.getInt(1) : 0;
        }
    }

    /**
     * Method: getTotalInventoryValue
     * Purpose: Calculates the inventory value from database quantities and prices
     * using BigDecimal arithmetic to avoid floating-point currency calculations.
     * Parameters: none.
     * Return: BigDecimal - the total value of all products in SQLite.
     */
    public BigDecimal getTotalInventoryValue() throws SQLException {
        ensureConnected();
        BigDecimal total = BigDecimal.ZERO;
        String sql = "SELECT product_id, product_name, category, supplier, "
                + "unit_quantity, unit_price FROM products";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                try {
                    Product product = readProduct(results);
                    total = total.add(product.getUnitPrice().multiply(
                            BigDecimal.valueOf(product.getUnitQuantity())));
                } catch (SQLException | IllegalArgumentException exception) {
                    // Invalid database rows are excluded from inventory valuation.
                }
            }
        }
        return total;
    }

    /**
     * Method: readProduct
     * Purpose: Converts the current SQLite result row into a validated Product object.
     * Parameters: results - the active query result row.
     * Return: Product - the product represented by the result row.
     */
    private Product readProduct(ResultSet results) throws SQLException {
        try {
            String priceText = results.getString("unit_price");
            if (priceText == null) {
                throw new SQLException("A product record is missing its unit price.");
            }
            int quantity = results.getInt("unit_quantity");
            if (results.wasNull()) {
                throw new SQLException("Quantity is missing.");
            }
            return new Product(
                    results.getString("product_id"),
                    results.getString("product_name"),
                    results.getString("category"),
                    results.getString("supplier"),
                    quantity,
                    new BigDecimal(priceText));
        } catch (IllegalArgumentException exception) {
            throw new SQLException(exception.getMessage(), exception);
        }
    }

    /**
     * Method: ensureConnected
     * Purpose: Prevents database operations from running when no SQLite connection is open.
     * Parameters: none.
     * Return: void.
     */
    private void ensureConnected() throws SQLException {
        if (!isConnected()) {
            throw new SQLException("Connect to an SQLite database before using inventory actions.");
        }
    }

    /**
     * Method: close
     * Purpose: Closes the current SQLite connection when the user exits or selects another database.
     * Parameters: none.
     * Return: void.
     */
    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        connection = null;
    }
}
