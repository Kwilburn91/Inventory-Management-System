package IMS; /**
 @authorKelly Wilburn
  * Course: CEN 3024 - Software Development 1
  * @since November 16, 2026
 * Class: IMS.DatabaseManager
 * Manages the user-selected SQLite inventory database using JDBC. Reads and validates rows as IMS.Product objects, tracks rejected records, performs database updates, and calculates inventory totals. Implements AutoCloseable to release its JDBC connection.
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
     * Opens the selected SQLite database and verifies the inventory table and required columns.
     * @param databasePath path to the existing SQLite database file
     * @throws SQLException if the database operation fails
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
     * Checks whether a database connection is currently active.
     * @return true if a database connection is active; otherwise false
     */
    public boolean isConnected() {

        try {

            return connection != null && !connection.isClosed();

        } catch (SQLException exception) {

            return false;

        }

    }

    /**
     * Retrieves valid inventory products from the connected database.
     * @return list of valid products retrieved from the database
     * @throws SQLException if the database operation fails
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

                    rejectedRecords.add("IMS.Product ID: " + (id == null ? "(missing)" : id)

                            + " — " + exception.getMessage());

                    rejectedRowIds.add(results.getLong("ims_rowid"));

                }

            }

        }

        return products;

    }

    /**
     * Returns descriptions of database rows that failed validation.
     * @return descriptions of rejected database records
     */
    public List<String> getRejectedRecords() {

        return new ArrayList<>(rejectedRecords);

    }

    /**
     * Permanently deletes the invalid database rows identified during validation.
     * @return number of invalid records deleted
     * @throws SQLException if the database operation fails
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
     * Looks up a product by its identifier.
     * @param productId seven-digit product identifier
     * @return matching product, or null if no match exists
     * @throws SQLException if the database operation fails
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
     * Looks up a product by its name.
     * @param productName name of the inventory product
     * @return matching product, or null if no match exists
     * @throws SQLException if the database operation fails
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
     * Inserts a validated product into the connected database.
     * @param product product record to insert
     * @return true if the product was inserted; otherwise false
     * @throws SQLException if the database operation fails
     */
    public boolean insertProduct(Product product) throws SQLException {

        ensureConnected();

        if (product == null) {

            throw new SQLException("IMS.Product information is missing.");

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
     * Updates the stored fields of the product identified by its ID.
     * @param productId seven-digit product identifier
     * @param productName name of the inventory product
     * @param category product category, containing at least one letter
     * @param supplier supplier name, containing at least one letter
     * @param unitQuantity nonnegative number of units in stock
     * @param unitPrice nonnegative price per unit
     * @return true if a matching product was updated; otherwise false
     * @throws SQLException if the database operation fails
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
     * Deletes the product identified by its ID.
     * @param productId seven-digit product identifier
     * @return true if a product was deleted; otherwise false
     * @throws SQLException if the database operation fails
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
     * Deletes the products identified by the supplied list of IDs.
     * @param productIds identifiers of products to delete
     * @return number of products deleted
     * @throws SQLException if the database operation fails
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
     * Counts the inventory products in the connected database.
     * @return number of inventory products
     * @throws SQLException if the database operation fails
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
     * Calculates the sum of quantity multiplied by unit price for inventory records.
     * @return total monetary value of inventory
     * @throws SQLException if the database operation fails
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

     * Purpose: Converts the current SQLite result row into a validated IMS.Product object.

     * Parameters: results - the active query result row.

     * Return: IMS.Product - the product represented by the result row.

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

    /**
     * Closes the active JDBC database connection.
     * @throws SQLException if the database operation fails
     */
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        connection = null;
    }
}
