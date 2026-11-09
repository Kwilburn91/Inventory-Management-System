/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * November 8, 2026
 * Class: GUI
 * Purpose: Provides the graphical interface for the Inventory Management System.
 * Users can add, import, search, modify, and remove products, view all inventory
 * records in a selectable table, and calculate the total value of the inventory.
 * Inventory operations and validation are performed by the existing IMS,
 * Product, and ProductValidator classes.
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
import java.awt.Insets;
import java.awt.GridLayout;
import java.io.File;
import java.math.BigDecimal;

public class GUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private final IMS inventoryManagementSystem;
    private final ProductValidator validator;

    private JTextField productIdField;
    private JTextField productNameField;
    private JTextField categoryField;
    private JTextField supplierField;
    private JTextField quantityField;
    private JTextField priceField;
    private JTextField filePathField;

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JLabel countLabel;
    private JLabel statusLabel;

    /**
     * Method: GUI
     * Purpose: Initializes the inventory and creates the application window.
     * Parameters: none.
     * Return: none (constructor).
     */
    public GUI() {
        inventoryManagementSystem = new IMS();
        validator = new ProductValidator();

        setTitle("IMS - Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 600));
        setMaximumSize(new Dimension(1600, 740));
        setSize(1080, 650);
        setLocationRelativeTo(null);

        createGUI();
        refreshTable();
        setVisible(true);
    }

    /**
     * Method: createGUI
     * Purpose: Builds the header, product-entry panel, file-import bar, inventory
     * table, operation controls, and status bar.
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
     * Purpose: Creates the application title and live inventory record count.
     * Parameters: none.
     * Return: JPanel - the header panel.
     */
    private JPanel createHeader() {
        // Paint a left-to-right gradient from white to the requested light blue.
        JPanel header = new JPanel(new BorderLayout(10, 4)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setPaint(new GradientPaint(
                        0, 0, Color.WHITE,
                        getWidth(), 0, new Color(203, 224, 242)));
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
     * Purpose: Creates the editable product form and its Add, Update, Search,
     * and Clear controls.
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

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        addButton.addActionListener(event -> addProduct());
        updateButton.addActionListener(event -> modifyProduct());
        searchButton.addActionListener(event -> searchProduct());
        clearButton.addActionListener(event -> clearForm());
        productIdField.addActionListener(event -> searchProduct());

        // Stack the four form controls vertically in a single column.
        JPanel buttons = new JPanel(new GridLayout(2, 2, 10, 12));
        JButton[] controls = {addButton, updateButton, searchButton, clearButton};
        for (JButton control : controls) {
            buttons.add(control);
        }

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
     * Purpose: Creates the file-import controls, selectable inventory table,
     * and bottom actions for removal, value calculation, and exit.
     * Parameters: none.
     * Return: JPanel - the inventory panel.
     */
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 10));

        JPanel filePanel = new JPanel(new BorderLayout(8, 0));
        filePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Load Inventory From Text File"),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        filePathField = new JTextField();
        JButton browseButton = new JButton("Browse...");
        JButton loadButton = new JButton("Load File");
        browseButton.addActionListener(event -> chooseFile());
        loadButton.addActionListener(event -> loadProducts());
        filePanel.add(new JLabel("Text file:"), BorderLayout.WEST);
        filePanel.add(filePathField, BorderLayout.CENTER);
        JPanel fileButtons = new JPanel(new BorderLayout(6, 0));
        fileButtons.add(browseButton, BorderLayout.WEST);
        fileButtons.add(loadButton, BorderLayout.EAST);
        filePanel.add(fileButtons, BorderLayout.EAST);

        String[] columns = {"Product ID", "Product Name", "Category", "Supplier", "Quantity", "Unit Price ($)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;

            @Override
            public Component prepareRenderer(
                    javax.swing.table.TableCellRenderer renderer,
                    int row,
                    int column) {
                Component cell = super.prepareRenderer(renderer, row, column);

                // Preserve the look-and-feel selection color and shade every other row.
                if (!isRowSelected(row)) {
                    cell.setBackground(row % 2 == 0
                            ? Color.WHITE
                            : new Color(232, 240, 244));
                }
                return cell;
            }
        };
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setRowHeight(29);
        productTable.setFillsViewportHeight(true);
        productTable.setAutoCreateRowSorter(true);
        productTable.getTableHeader().setReorderingAllowed(false);
        productTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && productTable.getSelectedRow() >= 0) {
                loadSelectedProductIntoForm();
            }
        });

        JScrollPane tableScroll = new JScrollPane(productTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Inventory Records"));

        JButton removeButton = new JButton("Remove Product");
        JButton valueButton = new JButton("Calculate Total Inventory Value");
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

        panel.add(filePanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Method: createStatusBar
     * Purpose: Creates the message area used to report selections and operation results.
     * Parameters: none.
     * Return: JPanel - the status bar.
     */
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 3, 0, 3));
        statusLabel = new JLabel("Select a product row or enter product details to begin.");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(statusLabel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Method: addProduct
     * Purpose: Validates the form and adds a new product when its ID and name are unique.
     * Parameters: none.
     * Return: void.
     */
    private void addProduct() {
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
        if (quantity == null) return;
        BigDecimal price = parsePrice(priceField.getText());
        if (price == null) return;

        if (inventoryManagementSystem.findProduct(productId) != null) {
            showError("That product ID already exists.");
            return;
        }
        if (inventoryManagementSystem.containsProductName(productName)) {
            showError("That product name already exists. Use Update to change the existing product.");
            return;
        }

        try {
            Product product = new Product(productId, productName, category, supplier, quantity, price);
            if (inventoryManagementSystem.addProduct(product)) {
                refreshTable();
                selectProduct(productId);
                setStatus("Product successfully added: " + productName);
            } else {
                showError("Product could not be added.");
            }
        } catch (IllegalArgumentException exception) {
            showError("Invalid product information: " + exception.getMessage());
        }
    }

    /**
     * Method: modifyProduct
     * Purpose: Validates the edited form values and updates the selected product.
     * Parameters: none.
     * Return: void.
     */
    private void modifyProduct() {
        String productId = productIdField.getText().trim();
        if (!validator.isValidProductId(productId)) {
            showError("Select a product row or enter a valid 7-digit product ID.");
            return;
        }
        Product existing = inventoryManagementSystem.findProduct(productId);
        if (existing == null) {
            showError("No product was found with that ID.");
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
        if (!productName.equalsIgnoreCase(existing.getProductName())
                && inventoryManagementSystem.containsProductName(productName)) {
            showError("That product name already exists.");
            return;
        }

        Integer quantity = parseQuantity(quantityField.getText());
        if (quantity == null) return;
        BigDecimal price = parsePrice(priceField.getText());
        if (price == null) return;

        boolean updated = inventoryManagementSystem.modifyProduct(
                productId, productName, category, supplier, quantity, price);
        if (updated) {
            refreshTable();
            selectProduct(productId);
            setStatus("Product successfully updated: " + productName);
        } else {
            showError("Product could not be updated. Check the product information.");
        }
    }

    /**
     * Method: loadProducts
     * Purpose: Imports valid product records from the text file in the path field
     * and reports successful, skipped, or empty-file results.
     * Parameters: none.
     * Return: void.
     */
    private void loadProducts() {
        String filePath = filePathField.getText().trim();
        if (filePath.length() >= 2 && filePath.startsWith("\"") && filePath.endsWith("\"")) {
            filePath = filePath.substring(1, filePath.length() - 1);
        }
        if (filePath.isEmpty()) {
            showError("File path cannot be empty.");
            return;
        }
        if (!filePath.toLowerCase().endsWith(".txt")) {
            showError("Please select a text file with a .txt extension.");
            return;
        }
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            showError("The specified text file could not be found.");
            return;
        }

        int addedCount = inventoryManagementSystem.loadProducts(filePath);
        if (addedCount == -1) {
            showError("The file could not be opened.");
            return;
        }
        refreshTable();
        if (inventoryManagementSystem.wasLastFileEmpty()) {
            setStatus("File processing complete. Document contained no entries.");
        } else {
            setStatus("File processing complete. Added: " + addedCount
                    + " | Skipped invalid or duplicate entries: "
                    + inventoryManagementSystem.getLastSkippedLineCount());
        }
    }

    /**
     * Method: chooseFile
     * Purpose: Opens a text-file chooser and places the selected path in the file field.
     * Parameters: none.
     * Return: void.
     */
    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Method: removeProduct
     * Purpose: Removes the selected product after confirmation, or asks for its ID
     * when no table row is selected.
     * Parameters: none.
     * Return: void.
     */
    private void removeProduct() {
        String productId = productIdField.getText().trim();
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = productTable.convertRowIndexToModel(selectedRow);
            productId = String.valueOf(tableModel.getValueAt(modelRow, 0));
        }
        if (!validator.isValidProductId(productId)) {
            productId = requestProductId("Enter the 7-digit product ID to remove:");
        }
        if (productId == null) return;

        Product product = inventoryManagementSystem.findProduct(productId);
        if (product == null) {
            showError("No product was found with that ID.");
            return;
        }
        int confirmation = JOptionPane.showConfirmDialog(this,
                productToString(product) + "\n\nRemove this product?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmation != JOptionPane.YES_OPTION) return;

        if (inventoryManagementSystem.removeProduct(productId)) {
            refreshTable();
            clearForm();
            setStatus("Product " + productId + " was removed.");
        } else {
            showError("Product could not be removed.");
        }
    }

    /**
     * Method: searchProduct
     * Purpose: Finds a product by its seven-digit ID, selects its table row,
     * and displays its details in the form.
     * Parameters: none.
     * Return: void.
     */
    private void searchProduct() {
        String productId = productIdField.getText().trim();
        if (!validator.isValidProductId(productId)) {
            productId = requestProductId("Enter the 7-digit product ID to search:");
        }
        if (productId == null) return;
        Product product = inventoryManagementSystem.findProduct(productId);
        if (product == null) {
            showError("No product was found with that ID.");
            return;
        }
        selectProduct(productId);
        setStatus("Product found: " + product.getProductName());
    }

    /**
     * Method: viewInventoryValue
     * Purpose: Calculates the total value of all inventory quantities multiplied
     * by their unit prices and displays the result.
     * Parameters: none.
     * Return: void.
     */
    private void viewInventoryValue() {
        BigDecimal total = inventoryManagementSystem.getTotalInventoryValue();
        String value = "$" + total.setScale(2).toPlainString();
        setStatus("Total Inventory Value: " + value);
        JOptionPane.showMessageDialog(this, "Total Inventory Value: " + value,
                "Inventory Value", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Method: refreshTable
     * Purpose: Rebuilds the table from the current inventory and updates the record count.
     * Parameters: none.
     * Return: void.
     */
    private void refreshTable() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        for (Product product : inventoryManagementSystem.getProductsAlphabetically()) {
            tableModel.addRow(new Object[]{
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategory(),
                    product.getSupplier(),
                    product.getUnitQuantity(),
                    product.getUnitPrice().setScale(2).toPlainString()
            });
        }
        countLabel.setText("Products: " + inventoryManagementSystem.getProductCount());
    }

    /**
     * Method: loadSelectedProductIntoForm
     * Purpose: Copies the selected table record into the editable product fields.
     * Parameters: none.
     * Return: void.
     */
    private void loadSelectedProductIntoForm() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) return;
        int row = productTable.convertRowIndexToModel(selectedRow);
        String id = String.valueOf(tableModel.getValueAt(row, 0));
        Product product = inventoryManagementSystem.findProduct(id);
        if (product == null) return;
        productIdField.setText(product.getProductId());
        productNameField.setText(product.getProductName());
        categoryField.setText(product.getCategory());
        supplierField.setText(product.getSupplier());
        quantityField.setText(String.valueOf(product.getUnitQuantity()));
        priceField.setText(product.getUnitPrice().toPlainString());
        setStatus("Selected product " + product.getProductId() + ". Change values and click Update, or use Remove Product.");
    }

    /**
     * Method: selectProduct
     * Purpose: Selects the row corresponding to a product ID and scrolls it into view.
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
     * Purpose: Clears all product-entry fields and removes the current table selection.
     * Parameters: none.
     * Return: void.
     */
    private void clearForm() {
        productIdField.setText("");
        productNameField.setText("");
        categoryField.setText("");
        supplierField.setText("");
        quantityField.setText("");
        priceField.setText("");
        productTable.clearSelection();
        setStatus("Form cleared. Enter product details to add a new record.");
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
     * Purpose: Prompts for a valid seven-digit product ID when one is not in the form.
     * Parameters: message - prompt displayed to the user.
     * Return: String - valid ID, or null if cancelled.
     */
    private String requestProductId(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, message, "Product ID", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return null;
            input = input.trim();
            if (validator.isValidProductId(input)) return input;
            showError("Product ID must contain exactly 7 digits.");
        }
    }

    /**
     * Method: productToString
     * Purpose: Formats a product's fields for confirmation and information dialogs.
     * Parameters: product - product to format.
     * Return: String - formatted product details.
     */
    private String productToString(Product product) {
        return "Product Name: " + product.getProductName()
                + "\nCategory: " + product.getCategory()
                + "\nSupplier: " + product.getSupplier()
                + "\nQuantity: " + product.getUnitQuantity()
                + "\nUnit Price: $" + product.getUnitPrice().setScale(2).toPlainString()
                + "\nProduct ID: " + product.getProductId();
    }

    /**
     * Method: showError
     * Purpose: Displays an error dialog and writes the message to the status bar.
     * Parameters: message - error description.
     * Return: void.
     */
    private void showError(String message) {
        setStatus(message);
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Method: setStatus
     * Purpose: Updates the status bar with the latest operation message.
     * Parameters: message - status text to display.
     * Return: void.
     */
    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Method: exitProgram
     * Purpose: Requests confirmation before closing the application.
     * Parameters: none.
     * Return: void.
     */
    private void exitProgram() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit the Inventory Management System?",
                "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            dispose();
        }
    }
}
