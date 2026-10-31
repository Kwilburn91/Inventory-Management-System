/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * November 1, 2026
 * GUI.java
 *
 * This class provides the graphical user interface for the Inventory Management System.
 * It allows users to:
 * - add products
 * - import products from text files
 * - search for products
 * - modify existing products
 * - remove products, display inventory
 * - calculate total inventory value
 * - exit the system
 * It communicates with the IMS, Product, and ProductValidator classes to perform inventory operations and validate user input.
 */

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.math.BigDecimal;

public class GUI extends JFrame {

    private final IMS inventoryManagementSystem;

    private final ProductValidator validator;

    private JTextArea outputArea;

    private JTextField productIdField;

    private JTextField productNameField;

    private JTextField categoryField;

    private JTextField supplierField;

    private JTextField quantityField;

    private JTextField priceField;

    private JTextField filePathField;

    /**
     * Method: GUI
     * Purpose: Initializes the inventory management system and configures the main application window. Creates the GUI components, displays the welcome message, and makes the window visible.
     * Parameters: none.
     * Return: void.
     */
    public GUI() {

        inventoryManagementSystem =
                new IMS();

        validator =
                new ProductValidator();

        setTitle(
                "IMS - Inventory Management System");

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        setSize(
                900,
                650);

        setLocationRelativeTo(null);

        createGUI();

        displayWelcomeMessage();

        setVisible(true);
    }

    /**
     * Method: createGUI
     * Purpose: Creates and organizes the main GUI components, including the application title, operation buttons, and scrollable output area.
     * Parameters: none.
     * Return: void.
     */
    private void createGUI() {

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10));

        JLabel titleLabel =
                new JLabel(
                        "IMS - Inventory Management System",
                        SwingConstants.CENTER);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24));

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH);

        JPanel operationsPanel =
                createOperationsPanel();

        mainPanel.add(
                operationsPanel,
                BorderLayout.WEST);

        outputArea =
                new JTextArea();

        outputArea.setEditable(false);

        outputArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14));

        outputArea.setLineWrap(true);

        outputArea.setWrapStyleWord(true);

        JScrollPane outputScrollPane =
                new JScrollPane(
                        outputArea);

        outputScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        ""));

        mainPanel.add(
                outputScrollPane,
                BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    /**
     * Method: createOperationsPanel
     * Purpose: Creates the vertical panel containing buttons for all available inventory operations.
     * Parameters: none.
     * Return: JPanel - the completed operations panel.
     */
    private JPanel createOperationsPanel() {

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS));

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Menu"));

        JButton addButton =
                new JButton(
                        "Add Item");

        JButton loadButton =
                new JButton(
                        "Load From File");

        JButton removeButton =
                new JButton(
                        "Remove Item");

        JButton searchButton =
                new JButton(
                        "Search Item");

        JButton modifyButton =
                new JButton(
                        "Modify Item");

        JButton viewButton =
                new JButton(
                        "Display All Items");

        JButton valueButton =
                new JButton(
                        "Inventory Value");

        JButton clearButton =
                new JButton(
                        "Clear Output");

        JButton exitButton =
                new JButton(
                        "Exit");

        addButton.addActionListener(
                event -> addProduct());

        loadButton.addActionListener(
                event -> loadProducts());

        removeButton.addActionListener(
                event -> removeProduct());

        searchButton.addActionListener(
                event -> searchProduct());

        modifyButton.addActionListener(
                event -> modifyProduct());

        viewButton.addActionListener(
                event -> viewProducts());

        valueButton.addActionListener(
                event -> viewInventoryValue());

        clearButton.addActionListener(
                event -> outputArea.setText(""));

        exitButton.addActionListener(
                event -> exitProgram());

        addButtonToPanel(panel, addButton);

        addButtonToPanel(panel, loadButton);

        addButtonToPanel(panel, removeButton);

        addButtonToPanel(panel, searchButton);

        addButtonToPanel(panel, modifyButton);

        addButtonToPanel(panel, viewButton);

        addButtonToPanel(panel, valueButton);

        addButtonToPanel(panel, clearButton);

        addButtonToPanel(panel, exitButton);

        return panel;
    }

    /**
     * Method: addButtonToPanel
     * Purpose: Adds an operation button to the operations panel, centers it horizontally, sets its maximum dimensions, and adds spacing between buttons.
     * Parameters: panel - the JPanel containing the operation buttons.
     * button - the JButton being added.
     * Return: void.
     */
    private void addButtonToPanel(
            JPanel panel,
            JButton button) {

        button.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        button.setMaximumSize(
                new Dimension(
                        180,
                        40));

        panel.add(button);

        panel.add(
                Box.createVerticalStrut(8));
    }

    /**
     * Method: displayWelcomeMessage
     * Purpose: Displays the application title and initial instructions in the GUI output area.
     * Parameters: none.
     * Return: void.
     */
    private void displayWelcomeMessage() {

        appendOutput(
                "================================================");
        appendOutput(
                "   Welcome To The Inventory Management System");
        appendOutput(
                "================================================");
        appendOutput(
                "");
        appendOutput(
                "   Please select an option from the menu.");
    }

    /**
     * Method: appendOutput
     * Purpose: Appends a message to the GUI output area and automatically scrolls to the newest message.
     * Parameters: message - the text to display.
     * Return: void.
     */
    private void appendOutput(
            String message) {

        outputArea.append(
                message + "\n");

        outputArea.setCaretPosition(
                outputArea.getDocument()
                        .getLength());
    }

    /**
     * Method: showError
     * Purpose: Displays an error dialog explaining invalid input or an unsuccessful inventory operation.
     * Parameters: message - the error message to display.
     * Return: void.
     */
    private void showError(
            String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Method: addProduct
     * Purpose: Collects product information through a GUI form, validates the entered values, checks for duplicate IDs and names, and adds the new product to the inventory.
     * Parameters: none.
     * Return: void.
     */
    private void addProduct() {

        JPanel panel =
                createProductInputPanel(
                        false,
                        null);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Add Item",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

        if (result !=
                JOptionPane.OK_OPTION) {

            return;
        }

        String productId =
                productIdField
                        .getText()
                        .trim();

        String productName =
                productNameField
                        .getText()
                        .trim();

        String category =
                categoryField
                        .getText()
                        .trim();

        String supplier =
                supplierField
                        .getText()
                        .trim();

        String quantityText =
                quantityField
                        .getText()
                        .trim();

        String priceText =
                priceField
                        .getText()
                        .trim();

        if (!validator.isValidProductId(
                productId)) {

            showError(
                    "Product ID must contain exactly 7 digits.");

            return;
        }

        if (!validator.isValidText(productName)) {

            showError(
                    "Product name cannot be empty.");

            return;
        }

        if (!validator.isValidCategory(category)) {

            showError(
                    "Category must contain at least one letter. "
                            + "Please enter a valid category.");

            return;
        }

        if (!validator.isValidSupplier(supplier)) {

            showError(
                    "Supplier name must contain at least one letter. "
                            + "Please enter a valid supplier name.");

            return;
        }

        Integer quantity =
                parseQuantity(
                        quantityText);

        if (quantity == null) {
            return;
        }

        BigDecimal price =
                parsePrice(
                        priceText);

        if (price == null) {
            return;
        }

        if (inventoryManagementSystem
                .findProduct(productId) != null) {

            showError(
                    "That product ID already exists.");

            return;
        }

        if (inventoryManagementSystem
                .containsProductName(productName)) {

            showError(
                    "That product name already exists. "
                            + "Use Modify Item to update the existing product.");

            return;
        }

        try {

            Product product =
                    new Product(
                            productId,
                            productName,
                            category,
                            supplier,
                            quantity,
                            price);

            boolean added =
                    inventoryManagementSystem
                            .addProduct(product);

            if (added) {

                appendOutput(
                        "\nItem successfully added.");

                displaySingleProduct(
                        product);

            } else {

                showError(
                        "Item could not be added.");
            }

        } catch (IllegalArgumentException exception) {

            showError(
                    "Invalid product information:\n"
                            + exception.getMessage());
        }
    }

    /**
     * Method: createProductInputPanel
     * Purpose: Creates the input form used to add or modify a product. When modifying, the form is populated with existing product information and the product ID is
     * made non-editable.
     * Parameters: modifying - indicates whether an existing product is modified.
     * currentProduct - the existing product, or null when adding.
     * Return: JPanel - the completed product input form.
     */
    private JPanel createProductInputPanel(
            boolean modifying,
            Product currentProduct) {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                0,
                                2,
                                8,
                                8));

        productIdField =
                new JTextField();

        productNameField =
                new JTextField();

        categoryField =
                new JTextField();

        supplierField =
                new JTextField();

        quantityField =
                new JTextField();

        priceField =
                new JTextField();

        if (currentProduct != null) {

            productIdField.setText(
                    currentProduct.getProductId());

            productNameField.setText(
                    currentProduct.getProductName());

            categoryField.setText(
                    currentProduct.getCategory());

            supplierField.setText(
                    currentProduct.getSupplier());

            quantityField.setText(
                    String.valueOf(
                            currentProduct.getUnitQuantity()));

            priceField.setText(
                    currentProduct.getUnitPrice()
                            .toPlainString());
        }

        if (modifying) {

            productIdField.setEditable(false);
        }

        panel.add(
                new JLabel("Product ID:"));

        panel.add(productIdField);

        panel.add(
                new JLabel("Product Name:"));

        panel.add(productNameField);

        panel.add(
                new JLabel("Category:"));

        panel.add(categoryField);

        panel.add(
                new JLabel("Supplier:"));

        panel.add(supplierField);

        panel.add(
                new JLabel("Quantity:"));

        panel.add(quantityField);

        panel.add(
                new JLabel("Unit Price:"));

        panel.add(priceField);

        return panel;
    }

    /**
     * Method: loadProducts
     * Purpose: Allows the user to select a text file and import valid products into the inventory. Reports successful additions, skipped entries, and file errors.
     * Parameters: none.
     * Return: void.
     */
    private void loadProducts() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                5,
                                5));

        JLabel label =
                new JLabel(
                        "Enter the location of the text file:");

        filePathField =
                new JTextField();

        JButton browseButton =
                new JButton(
                        "Browse...");

        browseButton.addActionListener(
                event -> chooseFile());

        JPanel pathPanel =
                new JPanel(
                        new BorderLayout(
                                5,
                                5));

        pathPanel.add(
                filePathField,
                BorderLayout.CENTER);

        pathPanel.add(
                browseButton,
                BorderLayout.EAST);

        panel.add(
                label,
                BorderLayout.NORTH);

        panel.add(
                pathPanel,
                BorderLayout.CENTER);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Load Items From File",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

        if (result !=
                JOptionPane.OK_OPTION) {

            return;
        }

        String filePath =
                filePathField
                        .getText()
                        .trim();

        if (filePath.length() >= 2
                && filePath.startsWith("\"")
                && filePath.endsWith("\"")) {

            filePath =
                    filePath.substring(
                            1,
                            filePath.length() - 1);
        }

        if (filePath.isEmpty()) {

            showError(
                    "File path cannot be empty.");

            return;
        }

        if (!filePath.toLowerCase().endsWith(".txt")) {

            showError(
                    "Please select a text file with a .txt extension.");

            return;
        }

        File file =
                new File(filePath);

        if (!file.exists()) {

            showError(
                    "The specified file does not exist.");

            return;
        }

        if (!file.isFile()) {

            showError(
                    "The specified path is not a file.");

            return;
        }

        try {

            int addedCount =
                    inventoryManagementSystem
                            .loadProducts(filePath);

            if (addedCount == -1) {

                showError(
                        "The file could not be opened.");

            } else {

                appendOutput(
                        "\n========== File Loading ==========");

                appendOutput(
                        "File processing complete.");

                if (inventoryManagementSystem.wasLastFileEmpty()) {

                    appendOutput(
                            "Document contained no entries.");

                } else {

                    appendOutput(
                            "Items successfully added: "
                                    + addedCount);

                    int skippedCount =
                            inventoryManagementSystem
                                    .getLastSkippedLineCount();

                    if (skippedCount > 0) {

                        appendOutput(
                                "Lines skipped due to invalid entries: "
                                        + skippedCount);
                    }
                }
            }

        } catch (RuntimeException exception) {

            showError(
                    "The file could not be processed:\n"
                            + exception.getMessage());
        }
    }

    /**
     * Method: chooseFile
     * Purpose: Opens a file chooser that allows the user to select a text file and places its absolute path into the file path input field.
     * Parameters: none.
     * Return: void.
     */
    private void chooseFile() {

        JFileChooser fileChooser =
                new JFileChooser();

        fileChooser.setFileFilter(
                new FileNameExtensionFilter(
                        "Text Files (*.txt)",
                        "txt"));

        int result =
                fileChooser.showOpenDialog(
                        this);

        if (result ==
                JFileChooser.APPROVE_OPTION) {

            filePathField.setText(
                    fileChooser
                            .getSelectedFile()
                            .getAbsolutePath());
        }
    }

    /**
     * Method: removeProduct
     * Purpose: Searches for a product using its ID, displays its information, and removes it only after the user confirms the removal.
     * Parameters: none.
     * Return: void.
     */
    private void removeProduct() {

        if (inventoryManagementSystem.getProductCount() == 0) {

            showError(
                    "There are currently no items in inventory.");

            return;
        }

        String productId =
                requestProductId(
                        "Enter the 7-digit product ID to remove:");

        if (productId == null) {
            return;
        }

        Product product =
                inventoryManagementSystem
                        .findProduct(productId);

        if (product == null) {

            showError(
                    "No product was found with that ID.");

            return;
        }

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        productToString(product)
                                + "\n\nRemove this product?",
                        "Confirm Remove",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

        if (confirmation !=
                JOptionPane.YES_OPTION) {

            appendOutput(
                    "\nProduct was not removed.");

            return;
        }

        boolean removed =
                inventoryManagementSystem
                        .removeProduct(productId);

        if (removed) {

            appendOutput(
                    "\nProduct successfully removed.");

            appendOutput(
                    "Product ID: "
                            + productId);

        } else {

            showError(
                    "Product could not be removed.");
        }
    }

    /**
     * Method: searchProduct
     * Purpose: Searches the inventory using a product ID and displays the matching product information.
     * Parameters: none.
     * Return: void.
     */
    private void searchProduct() {

        if (inventoryManagementSystem.getProductCount() == 0) {

            showError(
                    "There are currently no items in inventory.");

            return;
        }

        String productId =
                requestProductId(
                        "Enter the 7-digit product ID to search:");

        if (productId == null) {
            return;
        }

        Product product =
                inventoryManagementSystem
                        .findProduct(productId);

        if (product == null) {

            showError(
                    "No product was found with that ID.");

        } else {

            appendOutput(
                    "\n========== Product Found ==========");

            displaySingleProduct(
                    product);
        }
    }

    /**
     * Method: modifyProduct
     * Purpose: Locates an existing product, displays its current information, validates the replacement data, and updates the product through the IMS class.
     * Parameters: none.
     * Return: void.
     */
    private void modifyProduct() {

        if (inventoryManagementSystem.getProductCount() == 0) {

            showError(
                    "There are currently no items in inventory.");

            return;
        }

        String productId =
                requestProductId(
                        "Enter the 7-digit product ID to modify:");

        if (productId == null) {
            return;
        }

        Product product =
                inventoryManagementSystem
                        .findProduct(productId);

        if (product == null) {

            showError(
                    "No product was found with that ID.");

            return;
        }

        JPanel panel =
                createProductInputPanel(
                        true,
                        product);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Modify Item",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

        if (result !=
                JOptionPane.OK_OPTION) {

            return;
        }

        String productName =
                productNameField
                        .getText()
                        .trim();

        String category =
                categoryField
                        .getText()
                        .trim();

        String supplier =
                supplierField
                        .getText()
                        .trim();

        String quantityText =
                quantityField
                        .getText()
                        .trim();

        String priceText =
                priceField
                        .getText()
                        .trim();

        if (!validator.isValidText(productName)) {

            showError(
                    "Product name cannot be empty.");

            return;
        }

        if (!validator.isValidCategory(category)) {

            showError(
                    "Category must contain at least one letter. "
                            + "Please enter a valid category.");

            return;
        }

        if (!validator.isValidSupplier(supplier)) {

            showError(
                    "Supplier name must contain at least one letter. "
                            + "Please enter a valid supplier name.");

            return;
        }

        if (!productName.equalsIgnoreCase(product.getProductName())
                && inventoryManagementSystem.containsProductName(productName)) {

            showError(
                    "That product name already exists.");

            return;
        }

        Integer quantity =
                parseQuantity(
                        quantityText);

        if (quantity == null) {
            return;
        }

        BigDecimal price =
                parsePrice(
                        priceText);

        if (price == null) {
            return;
        }

        try {

            boolean modified =
                    inventoryManagementSystem
                            .modifyProduct(
                                    productId,
                                    productName,
                                    category,
                                    supplier,
                                    quantity,
                                    price);

            if (modified) {

                appendOutput(
                        "\nProduct successfully modified.");

                displaySingleProduct(
                        product);

            } else {

                showError(
                        "Product could not be modified.");
            }

        } catch (IllegalArgumentException exception) {

            showError(
                    "Invalid information:\n"
                            + exception.getMessage());
        }
    }

    /**
     * Method: viewProducts
     * Purpose: Retrieves and displays all inventory products in alphabetical order by product name. Displays an appropriate message when the inventory is empty.
     * Parameters: none.
     * Return: void.
     */
    private void viewProducts() {

        appendOutput(
                "\n========== Inventory ==========");

        if (inventoryManagementSystem
                .getProductCount() == 0) {

            appendOutput(
                    "There are currently no items in inventory.");

            return;
        }

        int itemNumber = 1;

        for (Product product :
                inventoryManagementSystem
                        .getProductsAlphabetically()) {

            appendOutput(
                    itemNumber
                            + ". "
                            + productToString(
                            product));

            appendOutput(
                    "----------------------------------------");

            itemNumber++;
        }
    }

    /**
     * Method: viewInventoryValue
     * Purpose: Retrieves and displays the total monetary value of all inventory quantities multiplied by their respective unit prices.
     * Parameters: none.
     * Return: void.
     */
    private void viewInventoryValue() {

        BigDecimal totalValue =
                inventoryManagementSystem
                        .getTotalInventoryValue();

        appendOutput(
                "\n========== Total Inventory Value ==========");

        appendOutput(
                "$"
                        + totalValue
                        .setScale(2)
                        .toPlainString());
    }

    /**
     * Method: displaySingleProduct
     * Purpose: Displays the formatted information for one product in the GUI output area.
     * Parameters: product - the product whose information is displayed.
     * Return: void.
     */
    private void displaySingleProduct(
            Product product) {

        appendOutput(
                productToString(
                        product));
    }

    /**
     * Method: productToString
     * Purpose: Converts a Product object into formatted text containing its name, category, supplier, quantity, unit price, and product ID.
     * Parameters: product - the Product object being formatted.
     * Return: String - the formatted product information.
     */
    private String productToString(
            Product product) {

        return
                "Product Name: "
                        + product.getProductName()
                        + "\n"
                        + "Category: "
                        + product.getCategory()
                        + "\n"
                        + "Supplier: "
                        + product.getSupplier()
                        + "\n"
                        + "Quantity: "
                        + product.getUnitQuantity()
                        + "\n"
                        + "Unit Price: $"
                        + product.getUnitPrice()
                        .setScale(2)
                        .toPlainString()
                        + "\n"
                        + "Product ID: "
                        + product.getProductId();
    }

    /**
     * Method: requestProductId
     * Purpose: Requests a product ID from the user and continues prompting until a valid seven-digit ID is entered or the user cancels the operation.
     * Parameters: message - the prompt displayed to the user.
     * Return: String - the valid product ID, or null if the user cancels the input dialog.
     */
    private String requestProductId(
            String message) {

        while (true) {

            String input =
                    JOptionPane.showInputDialog(
                            this,
                            message,
                            "Product ID",
                            JOptionPane.QUESTION_MESSAGE);

            if (input == null) {

                return null;
            }

            input =
                    input.trim();

            if (validator.isValidProductId(
                    input)) {

                return input;
            }

            showError(
                    "Product ID must contain exactly 7 digits.");
        }
    }

    /**
     * Method: parseQuantity
     * Purpose: Converts a quantity entered as text into an Integer and verifies that it is a non-negative whole number.
     * Parameters: quantityText - the quantity entered by the user.
     * Return: Integer - the valid quantity, or null
     * if the input is invalid.
     */
    private Integer parseQuantity(
            String quantityText) {

        if (quantityText == null
                || quantityText.trim().isEmpty()) {

            showError(
                    "Quantity cannot be empty.");

            return null;
        }

        try {

            int quantity =
                    Integer.parseInt(
                            quantityText.trim());

            if (!validator.isValidQuantity(
                    quantity)) {

                showError(
                        "Quantity must be a non-negative whole number (no decimals).");

                return null;
            }

            return quantity;

        } catch (NumberFormatException exception) {

            showError(
                    "Please enter a valid whole number.");

            return null;
        }
    }

    /**
     * Method: parsePrice
     * Purpose: Converts a price entered as text into
     * a BigDecimal and verifies that it is non-negative.
     * Parameters: priceText - the unit price entered by the user.
     * Return: BigDecimal - the valid price, or null
     * if the input is invalid.
     */
    private BigDecimal parsePrice(
            String priceText) {

        if (priceText == null
                || priceText.trim().isEmpty()) {

            showError(
                    "Price cannot be empty.");

            return null;
        }

        try {

            BigDecimal price =
                    new BigDecimal(
                            priceText.trim());

            if (!validator.isValidPrice(
                    price)) {

                showError(
                        "Price cannot be negative.");

                return null;
            }

            return price;

        } catch (NumberFormatException exception) {

            showError(
                    "Please enter a valid price.");

            return null;
        }
    }

    /**
     * Method: exitProgram
     * Purpose: Requests confirmation before closing the Inventory Management System window.
     * Parameters: none.
     * Return: void.
     */
    private void exitProgram() {

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to exit?",
                        "Exit",
                        JOptionPane.YES_NO_OPTION);

        if (result ==
                JOptionPane.YES_OPTION) {

            dispose();
        }
    }
}
