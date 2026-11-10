/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * November 8, 2026
 * Class: GUI
 * Purpose: Provides the graphical interface for the SQLite-based Inventory
 * Management System. Users connect to a database file they select, then add,
 * search, update, and remove products. Inventory records and calculations are
 * retrieved from and saved directly to SQLite rather than stored only in Java.
 */

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class GUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private final DatabaseManager databaseManager;
    private final ProductValidator validator;

    private JTextField productIdField;
    private JTextField productNameField;
    private JTextField categoryField;
    private JTextField supplierField;
    private JTextField quantityField;
    private JTextField priceField;
    private JTextField databasePathField;

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JLabel countLabel;
    private JLabel statusLabel;

    private JButton addButton;
    private JButton updateButton;
    private JButton searchButton;
    private JButton removeButton;
    private JButton valueButton;

    /**
     * Method: GUI
     * Purpose: Initializes the database manager and configures the main application window.
     * Parameters: none.
     * Return: none (constructor).
     */
    public GUI() {
        databaseManager = new DatabaseManager();
        validator = new ProductValidator();

        setTitle("IMS - Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 600));
        setMaximumSize(new Dimension(1600, 740));
        setSize(1080, 650);
        setLocationRelativeTo(null);

        createGUI();
        setDatabaseControlsEnabled(false);
        setStatus("Select your SQL database file, then click Connect.");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                try {
                    databaseManager.close();
                } catch (SQLException exception) {
                    setStatus("Database connection could not be closed cleanly.");
                }
            }
        });
        setVisible(true);
        javax.swing.SwingUtilities.invokeLater(() -> showEmptyTableMessage(false));
    }

    /**
     * Method: createGUI
     * Purpose: Builds the application header, product-entry panel, database
     * connection controls, inventory table, action buttons, and status bar.
     * Parameters: none.
     * Return: void.
     */
    private void createGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(createDetailsPanel(), BorderLayout.WEST);
        mainPanel.add(createInventoryPanel(), BorderLayout.CENTER);
        mainPanel.add(createStatusBar(), BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    /**
     * Method: createHeader
     * Purpose: Creates the application title and live database record count
     * using a vertical white-to-light-blue gradient banner.
     * Parameters: none.
     * Return: JPanel - the application header.
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 4)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setPaint(new GradientPaint(
                        0, 0, Color.WHITE,
                        0, getHeight(), new Color(203, 224, 242)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));
        header.setOpaque(false);

        JLabel title = new JLabel("IMS - Inventory Management System");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(new Color(25, 45, 75));

        countLabel = new JLabel("Products: 0", SwingConstants.RIGHT);
        countLabel.setFont(new Font("Arial", Font.BOLD, 16));
        countLabel.setForeground(new Color(25, 45, 75));

        header.add(title, BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);
        return header;
    }

    /**
     * Method: createDetailsPanel
     * Purpose: Creates the editable product form and its vertically stacked
     * Add, Update, Search, and Clear controls.
     * Parameters: none.
     * Return: JPanel - the product details panel.
     */
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Product Details"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        panel.setPreferredSize(new Dimension(315, 0));

        productIdField = new JTextField(18);
        productNameField = new JTextField(18);
        categoryField = new JTextField(18);
        supplierField = new JTextField(18);
        quantityField = new JTextField(18);
        priceField = new JTextField(18);

        addFormRow(panel, 0, "Product ID (7 digits)", productIdField);
        addFormRow(panel, 1, "Product Name", productNameField);
        addFormRow(panel, 2, "Category", categoryField);
        addFormRow(panel, 3, "Supplier", supplierField);
        addFormRow(panel, 4, "Quantity", quantityField);
        addFormRow(panel, 5, "Unit Price ($)", priceField);

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        addButton.addActionListener(event -> addProduct());
        updateButton.addActionListener(event -> modifyProduct());
        searchButton.addActionListener(event -> searchProduct());
        clearButton.addActionListener(event -> clearForm());
        productIdField.addActionListener(event -> searchProduct());

        JPanel buttons = new JPanel(new GridLayout(4, 1, 8, 7));
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(searchButton);
        buttons.add(clearButton);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 12;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(12, 2, 2, 2);
        panel.add(buttons, c);

        c.gridy = 13;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), c);
        return panel;
    }

    /**
     * Method: addFormRow
     * Purpose: Places one descriptive label and text field into the product form.
     * Parameters: panel - destination panel; row - grid row; labelText - field label;
     * field - text field accepting the value.
     * Return: void.
     */
    private void addFormRow(JPanel panel, int row, String labelText, JTextField field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row * 2;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(7, 2, 2, 2);
        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        panel.add(label, c);

        c.gridy = row * 2 + 1;
        c.insets = new Insets(0, 2, 3, 2);
        panel.add(field, c);
    }

    /**
     * Method: createInventoryPanel
     * Purpose: Creates the SQLite database connection controls, selectable
     * inventory table, and bottom actions for removal, value calculation, and exit.
     * Parameters: none.
     * Return: JPanel - the inventory panel.
     */
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 10));

        JPanel databasePanel = new JPanel(new BorderLayout(8, 0));
        databasePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Connect to SQL Database"),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        databasePathField = new JTextField();
        JButton browseButton = new JButton("Browse...");
        JButton connectButton = new JButton("Connect");
        browseButton.addActionListener(event -> chooseDatabase());
        connectButton.addActionListener(event -> connectDatabase());
        databasePanel.add(new JLabel("Database file:"), BorderLayout.WEST);
        databasePanel.add(databasePathField, BorderLayout.CENTER);
        JPanel databaseButtons = new JPanel(new BorderLayout(6, 0));
        databaseButtons.add(browseButton, BorderLayout.WEST);
        databaseButtons.add(connectButton, BorderLayout.EAST);
        databasePanel.add(databaseButtons, BorderLayout.EAST);

        String[] columns = {
                "Product ID", "Product Name", "Category", "Supplier", "Quantity", "Unit Price ($)"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;

            @Override
            public Component prepareRenderer(
                    javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component cell = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    cell.setBackground(row % 2 == 0
                            ? Color.WHITE
                            : new Color(232, 240, 244));
                }
                return cell;
            }
        };
        productTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        productTable.setRowHeight(29);
        productTable.setFillsViewportHeight(true);
        productTable.setAutoCreateRowSorter(true);
        productTable.getTableHeader().setReorderingAllowed(false);
        productTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                updateActionAvailability();
                if (productTable.getSelectedRowCount() == 1) {
                    loadSelectedProductIntoForm();
                } else if (productTable.getSelectedRowCount() > 1) {
                    setStatus(productTable.getSelectedRowCount()
                            + " products selected. Only Remove Product is available.");
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(productTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Inventory Records"));

        removeButton = new JButton("Remove Product");
        valueButton = new JButton("Calculate Total Inventory Value");
        JButton exitButton = new JButton("Exit");
        removeButton.addActionListener(event -> removeProduct());
        valueButton.addActionListener(event -> viewInventoryValue());
        exitButton.addActionListener(event -> exitProgram());

        JPanel actions = new JPanel(new BorderLayout(8, 0));
        JPanel leftActions = new JPanel(new BorderLayout(8, 0));
        leftActions.add(removeButton, BorderLayout.WEST);
        leftActions.add(valueButton, BorderLayout.CENTER);
        actions.add(leftActions, BorderLayout.WEST);
        actions.add(exitButton, BorderLayout.EAST);

        panel.add(databasePanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Method: createStatusBar
     * Purpose: Creates the message area used to report database status,
     * selections, and inventory operation results.
     * Parameters: none.
     * Return: JPanel - the status bar.
     */
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 3, 0, 3));
        statusLabel = new JLabel("Select your database file, then click Connect.");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(statusLabel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Method: chooseDatabase
     * Purpose: Opens a file chooser and places the user's selected SQLite database path in the connection field.
     * Parameters: none.
     * Return: void.
     */
    private void chooseDatabase() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Database Files (*.db, *.sqlite, *.sqlite3)",
                "db", "sqlite", "sqlite3"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            databasePathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Method: connectDatabase
     * Purpose: Uses the path entered or selected by the user to connect to
     * the supplied SQLite database and load its existing inventory records.
     * Parameters: none.
     * Return: void.
     */
    private void connectDatabase() {
        String databasePath = databasePathField.getText().trim();
        if (databasePath.length() >= 2
                && databasePath.startsWith("\"")
                && databasePath.endsWith("\"")) {
            databasePath = databasePath.substring(1, databasePath.length() - 1);
        }
        if (databasePath.isEmpty()) {
            showError("Enter the path to your database file.");
            return;
        }

        File databaseFile = new File(databasePath);
        if (!databaseFile.exists() || !databaseFile.isFile()) {
            showError("The selected database file could not be found. Check the path and try again.");
            return;
        }

        try {
            databaseManager.close();
            databaseManager.connect(databasePath);
            refreshTable();
            List<String> rejected = databaseManager.getRejectedRecords();
            setStatus("Connected: " + databaseFile.getName() + " | Valid: "
                    + tableModel.getRowCount() + " | Rejected: " + rejected.size());
            if (!rejected.isEmpty()) {
                promptToRemoveRejectedRecords(rejected);
            }
            if (tableModel.getRowCount() == 0) {
                showEmptyTableMessage(true);
            }
        } catch (SQLException exception) {
            setDatabaseControlsEnabled(false);
            showError("Unable to connect to the SQL database.\n\n"
                    + exception.getMessage());
        }
    }

    /**
     * Method: setDatabaseControlsEnabled
     * Purpose: Enables database-dependent buttons only after a valid SQLite connection is established.
     * Parameters: enabled - whether database actions should be available.
     * Return: void.
     */
    private void setDatabaseControlsEnabled(boolean enabled) {
        addButton.setEnabled(enabled);
        updateButton.setEnabled(enabled && tableModel.getRowCount() > 0);
        searchButton.setEnabled(enabled && tableModel.getRowCount() > 0);
        removeButton.setEnabled(enabled && tableModel.getRowCount() > 0);
        valueButton.setEnabled(enabled);
    }

    /**
     * Method: updateActionAvailability
     * Purpose: Disables record-dependent actions when the displayed inventory is empty.
     * Parameters: none.
     * Return: void.
     */
    private void updateActionAvailability() {
        setDatabaseControlsEnabled(databaseManager.isConnected());
    }

    /**
     * Method: showEmptyTableMessage
     * Purpose: Explains whether the user needs to connect to a database or add
     * products to a connected database that has no valid inventory records.
     * Parameters: connected - whether a database is currently connected.
     * Return: void.
     */
    private void showEmptyTableMessage(boolean connected) {
        String message = connected
                ? "The inventory table is empty. \nPlease add a product to the connected SQL database."
                : "The inventory table is empty. \nSelect and connect to an SQL database.";
        setStatus(message);
        JOptionPane.showMessageDialog(this, message, "Empty Inventory",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Method: rejectMultipleSelection
     * Purpose: Prevents non-removal operations when multiple table rows are selected.
     * Parameters: none.
     * Return: boolean - true if the operation must be cancelled.
     */
    private boolean rejectMultipleSelection() {
        if (productTable.getSelectedRowCount() > 1) {
            showError("This function cannot be performed while multiple items are selected. "
                    + "Select one item or deselect the rows.");
            return true;
        }
        return false;
    }

    /**
     * Method: addProduct
     * Purpose: Validates the form, prevents duplicate IDs and names, and inserts
     * a new product record directly into the connected SQLite database.
     * Parameters: none.
     * Return: void.
     */
    private void addProduct() {
        if (rejectMultipleSelection()) {
            return;
        }
        if (!requireDatabaseConnection()) {
            return;
        }

        String productId = productIdField.getText().trim();
        String productName = productNameField.getText().trim();
        String category = categoryField.getText().trim();
        String supplier = supplierField.getText().trim();

        if (!validator.isValidProductId(productId)) {
            showError("Product ID must contain exactly 7 digits.");
            productIdField.requestFocus();
            return;
        }
        if (!validator.isValidText(productName)) {
            showError("Product name cannot be empty.");
            productNameField.requestFocus();
            return;
        }
        if (!validator.isValidCategory(category)) {
            showError("Category must contain at least one letter.");
            categoryField.requestFocus();
            return;
        }
        if (!validator.isValidSupplier(supplier)) {
            showError("Supplier name must contain at least one letter.");
            supplierField.requestFocus();
            return;
        }

        Integer quantity = parseQuantity(quantityField.getText());
        if (quantity == null) {
            return;
        }
        BigDecimal price = parsePrice(priceField.getText());
        if (price == null) {
            return;
        }

        try {
            if (databaseManager.findProduct(productId) != null) {
                showError("That product ID already exists in the database.");
                return;
            }
            if (databaseManager.findProductByName(productName) != null) {
                showError("That product name already exists in the database.");
                return;
            }

            Product product = new Product(productId, productName, category,
                    supplier, quantity, price);
            if (databaseManager.insertProduct(product)) {
                refreshTable();
                selectProduct(productId);
                setStatus("Product successfully added to the database: " + productName);
            } else {
                showError("The product could not be inserted into the database.");
            }
        } catch (SQLException | IllegalArgumentException exception) {
            showError("Unable to add product: " + exception.getMessage());
        }
    }

    /**
     * Method: modifyProduct
     * Purpose: Validates the edited form values and updates the selected product
     * directly in SQLite without replacing the database with Java-only data.
     * Parameters: none.
     * Return: void.
     */
    private void modifyProduct() {
        if (rejectMultipleSelection()) {
            return;
        }
        if (!requireDatabaseConnection()) {
            return;
        }

        String productId = productIdField.getText().trim();
        if (!validator.isValidProductId(productId)) {
            showError("Select a product row or enter a valid 7-digit product ID.");
            return;
        }

        String productName = productNameField.getText().trim();
        String category = categoryField.getText().trim();
        String supplier = supplierField.getText().trim();
        if (!validator.isValidText(productName)) {
            showError("Product name cannot be empty.");
            return;
        }
        if (!validator.isValidCategory(category)) {
            showError("Category must contain at least one letter.");
            return;
        }
        if (!validator.isValidSupplier(supplier)) {
            showError("Supplier name must contain at least one letter.");
            return;
        }

        Integer quantity = parseQuantity(quantityField.getText());
        if (quantity == null) {
            return;
        }
        BigDecimal price = parsePrice(priceField.getText());
        if (price == null) {
            return;
        }

        try {
            if (databaseManager.findProduct(productId) == null) {
                showError("No product was found with that ID.");
                return;
            }
            boolean updated = databaseManager.updateProduct(
                    productId, productName, category, supplier, quantity, price);
            if (updated) {
                refreshTable();
                selectProduct(productId);
                setStatus("Product successfully updated in the database: " + productName);
            } else {
                showError("The selected database record could not be updated.");
            }
        } catch (SQLException exception) {
            showError("Unable to update product: " + exception.getMessage());
        }
    }

    /**
     * Method: removeProduct
     * Purpose: Deletes the selected product from SQLite after the user confirms
     * the removal, or requests a product ID when no table row is selected.
     * Parameters: none.
     * Return: void.
     */
    private void removeProduct() {
        if (!requireDatabaseConnection()) {
            return;
        }

        int[] selectedRows = productTable.getSelectedRows();
        List<String> productIds = new ArrayList<>();
        for (int selectedRow : selectedRows) {
            int modelRow = productTable.convertRowIndexToModel(selectedRow);
            productIds.add(String.valueOf(tableModel.getValueAt(modelRow, 0)));
        }

        if (productIds.isEmpty()) {
            String productId = productIdField.getText().trim();
            if (!validator.isValidProductId(productId)) {
                productId = requestProductId("Enter the 7-digit product ID to remove:");
            }
            if (productId == null) {
                return;
            }
            productIds.add(productId);
        }

        try {
            if (productIds.size() == 1 && databaseManager.findProduct(productIds.get(0)) == null) {
                showError("No product was found with that ID.");
                return;
            }

            String message = productIds.size() == 1
                    ? "Remove product " + productIds.get(0) + " from the database?"
                    : "Permanently remove these " + productIds.size()
                    + " products from the database?";
            int confirmation = JOptionPane.showConfirmDialog(this, message,
                    "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirmation != JOptionPane.YES_OPTION) {
                setStatus("Product removal cancelled.");
                return;
            }

            int removed = databaseManager.deleteProducts(productIds);
            refreshTable();
            clearForm();
            setStatus(removed + " product(s) deleted from the database.");
            if (tableModel.getRowCount() == 0) {
                showEmptyTableMessage(true);
            }
        } catch (SQLException exception) {
            showError("Unable to remove product(s): " + exception.getMessage());
        }
    }

    /**
     * Method: searchProduct
     * Purpose: Searches SQLite by product ID, selects the matching table row,
     * and loads the database record into the form.
     * Parameters: none.
     * Return: void.
     */
    private void searchProduct() {
        if (rejectMultipleSelection()) {
            return;
        }
        if (!requireDatabaseConnection()) {
            return;
        }

        String productId = productIdField.getText().trim();
        if (!validator.isValidProductId(productId)) {
            productId = requestProductId("Enter the 7-digit product ID to search:");
        }
        if (productId == null) {
            return;
        }

        try {
            Product product = databaseManager.findProduct(productId);
            if (product == null) {
                showError("No product was found with that ID.");
                return;
            }
            selectProduct(productId);
            setStatus("Product found in the database: " + product.getProductName());
        } catch (SQLException exception) {
            showError("Unable to search the database: " + exception.getMessage());
        }
    }

    /**
     * Method: viewInventoryValue
     * Purpose: Calculates the total inventory value from the current SQLite
     * records and displays the result in the GUI.
     * Parameters: none.
     * Return: void.
     */
    private void viewInventoryValue() {
        if (rejectMultipleSelection()) {
            return;
        }
        if (!requireDatabaseConnection()) {
            return;
        }

        try {
            BigDecimal total = databaseManager.getTotalInventoryValue();
            String value = "$" + total.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
            setStatus("Total Inventory Value: " + value);
            JOptionPane.showMessageDialog(this, "Total Inventory Value: " + value,
                    "Inventory Value", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException exception) {
            showError("Unable to calculate inventory value: " + exception.getMessage());
        }
    }

    /**
     * Method: refreshTable
     * Purpose: Reloads the table from SQLite and updates the record count so
     * the GUI reflects the current contents of the connected database.
     * Parameters: none.
     * Return: void.
     */
    private void refreshTable() {
        if (tableModel == null || !databaseManager.isConnected()) {
            return;
        }
        try {
            tableModel.setRowCount(0);
            List<Product> products = databaseManager.getAllProducts();
            for (Product product : products) {
                tableModel.addRow(new Object[]{
                        product.getProductId(),
                        product.getProductName(),
                        product.getCategory(),
                        product.getSupplier(),
                        product.getUnitQuantity(),
                        product.getUnitPrice().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()
                });
            }
            countLabel.setText("Valid: " + products.size()
                    + " | Rejected: " + databaseManager.getRejectedRecords().size());
            updateActionAvailability();
        } catch (SQLException exception) {
            showError("Unable to retrieve database records: " + exception.getMessage());
        }
    }

    /**
     * Method: showRejectedRecords
     * Purpose: Displays the rejected database records separately from the valid
     * inventory table without deleting or modifying their source rows.
     * Parameters: rejected - descriptions of invalid records.
     * Return: void.
     */
    private void showRejectedRecords(List<String> rejected) {
        javax.swing.JTextArea report = new javax.swing.JTextArea(12, 65);
        report.setEditable(false);
        report.setLineWrap(true);
        report.setWrapStyleWord(true);
        StringBuilder message = new StringBuilder("Valid products loaded: ")
                .append(tableModel.getRowCount())
                .append("\nInvalid records rejected: ").append(rejected.size())
                .append("\n\n");
        for (String detail : rejected) {
            message.append(detail).append("\n");
        }
        report.setText(message.toString());
        report.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(report),
                "Database Validation Report", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Method: promptToRemoveRejectedRecords
     * Purpose: Shows a validation report and asks whether to permanently remove
     * invalid rows from the connected SQLite database. Choosing No retains them.
     * Parameters: rejected - invalid record descriptions from the latest scan.
     * Return: void.
     */
    private void promptToRemoveRejectedRecords(List<String> rejected) {
        javax.swing.JTextArea report = new javax.swing.JTextArea(12, 65);
        report.setEditable(false);
        report.setLineWrap(true);
        report.setWrapStyleWord(true);
        StringBuilder message = new StringBuilder("Valid products loaded: ")
                .append(tableModel.getRowCount())
                .append("\nInvalid records found: ").append(rejected.size())
                .append("\n\n");
        for (String detail : rejected) {
            message.append(detail).append("\n");
        }
        report.setText(message.toString());
        report.setCaretPosition(0);

        JPanel dialogContent = new JPanel(new BorderLayout(0, 12));
        dialogContent.add(new JScrollPane(report), BorderLayout.CENTER);
        JLabel question = new JLabel(
                "Do you want to permanently remove the invalid entries?",
                SwingConstants.CENTER);
        question.setFont(question.getFont().deriveFont(Font.BOLD));
        dialogContent.add(question, BorderLayout.SOUTH);

        int answer = JOptionPane.showConfirmDialog(this, dialogContent,
                "Invalid Database Entries - Remove?",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            try {
                int removed = databaseManager.deleteRejectedRecords();
                refreshTable();
                setStatus("Removed " + removed + " invalid database record(s). "
                        + "Rejected: " + databaseManager.getRejectedRecords().size());
                JOptionPane.showMessageDialog(this,
                        removed + " invalid record(s) permanently removed from database.",
                        "Invalid Entries Removed", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException exception) {
                showError("Unable to remove invalid records: " + exception.getMessage());
                refreshTable();
            }
        } else {
            setStatus("Invalid records retained in database. Valid: "
                    + tableModel.getRowCount() + " | Rejected: " + rejected.size());
        }
    }

    /**
     * Method: loadSelectedProductIntoForm
     * Purpose: Retrieves the selected product from SQLite and copies its values
     * into the editable product fields.
     * Parameters: none.
     * Return: void.
     */
    private void loadSelectedProductIntoForm() {
        int selectedRow = productTable.getSelectedRow();
        if (productTable.getSelectedRowCount() != 1 || !databaseManager.isConnected()) {
            return;
        }
        int row = productTable.convertRowIndexToModel(selectedRow);
        String id = String.valueOf(tableModel.getValueAt(row, 0));
        try {
            Product product = databaseManager.findProduct(id);
            if (product == null) {
                return;
            }
            productIdField.setText(product.getProductId());
            productNameField.setText(product.getProductName());
            categoryField.setText(product.getCategory());
            supplierField.setText(product.getSupplier());
            quantityField.setText(String.valueOf(product.getUnitQuantity()));
            priceField.setText(product.getUnitPrice().setScale(2,
                    java.math.RoundingMode.HALF_UP).toPlainString());
            setStatus("Selected database record " + product.getProductId()
                    + ". Edit values and click Update, or use Remove Product.");
        } catch (SQLException exception) {
            showError("Unable to retrieve the selected product: " + exception.getMessage());
        }
    }

    /**
     * Method: selectProduct
     * Purpose: Selects the table row matching a product ID and scrolls it into view.
     * Parameters: productId - the product identifier to select.
     * Return: void.
     */
    private void selectProduct(String productId) {
        for (int modelRow = 0; modelRow < tableModel.getRowCount(); modelRow++) {
            if (productId.equals(String.valueOf(tableModel.getValueAt(modelRow, 0)))) {
                int viewRow = productTable.convertRowIndexToView(modelRow);
                productTable.setRowSelectionInterval(viewRow, viewRow);
                productTable.scrollRectToVisible(productTable.getCellRect(viewRow, 0, true));
                return;
            }
        }
    }

    /**
     * Method: clearForm
     * Purpose: Clears the product-entry fields and removes the current table selection.
     * Parameters: none.
     * Return: void.
     */
    private void clearForm() {
        if (rejectMultipleSelection()) {
            return;
        }
        productIdField.setText("");
        productNameField.setText("");
        categoryField.setText("");
        supplierField.setText("");
        quantityField.setText("");
        priceField.setText("");
        productTable.clearSelection();
        setStatus("Form cleared. Enter product details to add a new database record.");
        productIdField.requestFocusInWindow();
    }

    /**
     * Method: parseQuantity
     * Purpose: Converts quantity text into a non-negative whole number.
     * Parameters: text - quantity entered in the form.
     * Return: Integer - valid quantity, or null when invalid.
     */
    private Integer parseQuantity(String text) {
        if (text == null || text.trim().isEmpty()) {
            showError("Quantity cannot be empty.");
            return null;
        }
        try {
            int quantity = Integer.parseInt(text.trim());
            if (!validator.isValidQuantity(quantity)) {
                showError("Quantity must be a non-negative whole number (no decimals).");
                return null;
            }
            return quantity;
        } catch (NumberFormatException exception) {
            showError("Please enter a valid whole number.");
            return null;
        }
    }

    /**
     * Method: parsePrice
     * Purpose: Converts price text into a non-negative BigDecimal amount.
     * Parameters: text - unit price entered in the form.
     * Return: BigDecimal - valid price, or null when invalid.
     */
    private BigDecimal parsePrice(String text) {
        if (text == null || text.trim().isEmpty()) {
            showError("Unit price cannot be empty.");
            return null;
        }
        try {
            BigDecimal price = new BigDecimal(text.trim());
            if (!validator.isValidPrice(price)) {
                showError("Price cannot be negative.");
                return null;
            }
            return price;
        } catch (NumberFormatException exception) {
            showError("Please enter a valid price.");
            return null;
        }
    }

    /**
     * Method: requestProductId
     * Purpose: Requests a valid seven-digit product ID when one is not entered in the form.
     * Parameters: message - prompt displayed to the user.
     * Return: String - valid ID, or null if the user cancels.
     */
    private String requestProductId(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, message,
                    "Product ID", JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return null;
            }
            input = input.trim();
            if (validator.isValidProductId(input)) {
                return input;
            }
            showError("Product ID must contain exactly 7 digits.");
        }
    }

    /**
     * Method: productToString
     * Purpose: Formats a Product object for the removal-confirmation dialog.
     * Parameters: product - the product being displayed.
     * Return: String - the formatted product details.
     */
    private String productToString(Product product) {
        return "Product Name: " + product.getProductName()
                + "\nCategory: " + product.getCategory()
                + "\nSupplier: " + product.getSupplier()
                + "\nQuantity: " + product.getUnitQuantity()
                + "\nUnit Price: $" + product.getUnitPrice()
                .setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()
                + "\nProduct ID: " + product.getProductId();
    }

    /**
     * Method: requireDatabaseConnection
     * Purpose: Ensures that an inventory operation cannot run until the user has connected to a valid SQLite database.
     * Parameters: none.
     * Return: boolean - true when connected; otherwise false.
     */
    private boolean requireDatabaseConnection() {
        if (!databaseManager.isConnected()) {
            showError("Connect to your database before using this operation.");
            return false;
        }
        return true;
    }

    /**
     * Method: showError
     * Purpose: Displays an error message in the GUI and updates the status bar.
     * Parameters: message - the explanation shown to the user.
     * Return: void.
     */
    private void showError(String message) {
        setStatus(message);
        JOptionPane.showMessageDialog(this, message,
                "Inventory Management System", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Method: setStatus
     * Purpose: Updates the GUI status bar with the latest operation result or instruction.
     * Parameters: message - text displayed in the status bar.
     * Return: void.
     */
    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    /**
     * Method: exitProgram
     * Purpose: Requests confirmation before closing the application and its database connection.
     * Parameters: none.
     * Return: void.
     */
    private void exitProgram() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Exit", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            try {
                databaseManager.close();
            } catch (SQLException exception) {
                showError("The database connection could not be closed cleanly.");
            }
            dispose();
        }
    }
}