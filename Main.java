/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * Date: October 17, 2026
 * Class: Main
 * Purpose: Runs the Inventory Management System application. It presents the menu,
 * accepts and validates user input, and coordinates inventory operations so users can:
 * add, import, search, modify, remove, display products, and display total value of
 * inventory or exit the program.
 */
import java.math.BigDecimal;
import java.util.Scanner;

public class Main {

    /**
     * Method: main
     * Purpose: Starts the application by creating the Main object and invoking its run method.
     * Arguments: args.
     * Returns: none (void method).
     */
    public static void main(String[] args) {

        Main application =
                new Main();

        application.run();
    }

    /**
     * Method: run
     * Purpose: Controls the inventory application lifecycle, displays the menu, and dispatches each selected operation.
     * Arguments: none.
     * Returns: String.
     */
    public String run() {

        IMS inventoryManagementSystem =
                new IMS();

        Scanner scanner =
                new Scanner(System.in);

        displayTitle();

        boolean running = true;

        while (running) {

            displayMenu();

            int menuChoice =
                    getMenuChoice(scanner);

            switch (menuChoice) {

                case 1:

                    addProduct(
                            inventoryManagementSystem,
                            scanner);

                    break;

                case 2:

                    loadProducts(
                            inventoryManagementSystem,
                            scanner);

                    break;

                case 3:

                    if (inventoryManagementSystem.getProductCount() == 0) {
                        System.out.println(
                                "There are currently no items in inventory.");
                        break;
                    }

                    removeProduct(
                            inventoryManagementSystem,
                            scanner);

                    break;

                case 4:

                    if (inventoryManagementSystem.getProductCount() == 0) {
                        System.out.println(
                                "There are currently no items in inventory.");
                        break;
                    }

                    searchProduct(
                            inventoryManagementSystem,
                            scanner);

                    break;

                case 5:

                    if (inventoryManagementSystem.getProductCount() == 0) {
                        System.out.println(
                                "There are currently no items in inventory.");
                        break;
                    }

                    modifyProduct(
                            inventoryManagementSystem,
                            scanner);

                    break;

                case 6:

                    if (inventoryManagementSystem.getProductCount() == 0) {
                        System.out.println(
                                "There are currently no items in inventory.");
                        break;
                    }

                    viewProducts(
                            inventoryManagementSystem);

                    break;

                case 7:

                    viewInventoryValue(
                            inventoryManagementSystem);

                    break;

                case 8:

                    running = false;

                    System.out.println(
                            "\nThank you for using the "
                                    + "Inventory Management System.");

                    break;

                default:

                    System.out.println(
                            "\nInvalid menu choice.");

                    System.out.println(
                            "Please enter a number from 1 through 8.");
            }
        }

        scanner.close();

        return "Inventory Management System closed.";
    }

    /**
     * Method: displayTitle
     * Purpose: Prints the application title and returns the title text.
     * Arguments: none.
     * Returns: String.
     */
    private String displayTitle() {

        String title =
                "========================================\n"
                        + "   IMS - Inventory Management System\n"
                        + "========================================";

        System.out.println(title);

        return title;
    }

    /**
     * Method: displayMenu
     * Purpose: Prints the available inventory operations and returns the menu text.
     * Arguments: none.
     * Returns: String.
     */
    private String displayMenu() {

        String menu =
                "\n----------------------------------------\n"
                        + "                 MENU\n"
                        + "----------------------------------------\n"
                        + "1. Add Item Manually\n"
                        + "2. Add Item(s) From A Text File\n"
                        + "3. Remove Item\n"
                        + "4. Search Item\n"
                        + "5. Modify Item\n"
                        + "6. Display All Items\n"
                        + "7. Display Total Inventory Value\n"
                        + "8. Exit\n"
                        + "----------------------------------------";

        System.out.println(menu);

        return menu;
    }

    /**
     * Method: getMenuChoice
     * Purpose: Reads and returns a valid integer menu choice from the user.
     * Arguments: scanner.
     * Returns: int.
     */
    private int getMenuChoice(
            Scanner scanner) {

        while (!scanner.hasNextInt()) {

            System.out.println(
                    "Invalid input. "
                            + "Please enter a number from 1 through 8.");

            scanner.nextLine();
        }

        int menuChoice =
                scanner.nextInt();

        scanner.nextLine();

        return menuChoice;
    }

    /**
     * Method: addProduct
     * Purpose: Adds a product to inventory unless the input is null or its identifier already exists.
     * Arguments: inventoryManagementSystem, scanner.
     * Returns: boolean.
     */
    private boolean addProduct(
            IMS inventoryManagementSystem,
            Scanner scanner) {

        System.out.println(
                "\n========== Add Item ==========");

        String productId =
                getProductId(scanner);

        if (inventoryManagementSystem
                .findProduct(productId) != null) {

            System.out.println(
                    "Item has already been added to the list.");

            System.out.println(
                    "Select option '5. Modify Item' to update quantity or price listing.");

            return false;
        }

        String productName =
                getRequiredText(
                        scanner,
                        "Enter product name: ");

        String category =
                getRequiredCategory(scanner);

        String supplier =
                getRequiredSupplier(scanner);

        int unitQuantity =
                getQuantity(
                        scanner,
                        "Enter quantity: ");

        BigDecimal unitPrice =
                getPrice(
                        scanner,
                        "Enter unit price: ");

        try {

            Product product =
                    new Product(
                            productId,
                            productName,
                            category,
                            supplier,
                            unitQuantity,
                            unitPrice);

            boolean added =
                    inventoryManagementSystem
                            .addProduct(product);

            if (added) {

                System.out.println(
                        "Item successfully added.");

            } else {

                System.out.println();
                System.out.println(
                        "Item has already been added to the list.");
                System.out.println(
                        "Item could not be added.");
            }

            return added;

        } catch (IllegalArgumentException exception) {

            System.out.println(
                    "Invalid product information: "
                            + exception.getMessage());

            return false;
        }
    }

    /**
     * Method: loadProducts
     * Purpose: Reads product records from a text file, adds valid records, and tracks skipped entries.
     * Arguments: inventoryManagementSystem, scanner.
     * Returns: int.
     */
    private int loadProducts(
            IMS inventoryManagementSystem,
            Scanner scanner) {

        System.out.println(
                "\n========== Load Items From File ==========");

        System.out.print(
                "Enter the file path: ");

        String filePath =
                scanner.nextLine().trim();

        if (filePath.length() >= 2
                && filePath.startsWith("\"")
                && filePath.endsWith("\"")) {

            filePath =
                    filePath.substring(
                            1,
                            filePath.length() - 1);
        }

        if (filePath.isEmpty()) {

            System.out.println(
                    "The file could not be opened.");

            System.out.println(
                    "Please check that the file type is a text file");

            return -1;
        }

        // Reject files that do not have a .txt extension.
        if (!filePath.matches("(?s).*\\.txt")) {

            System.out.println(
                    "The file could not be opened.");

            System.out.println(
                    "Please check that the file type is a text file");

            return -1;
        }

        int addedCount =
                inventoryManagementSystem
                        .loadProducts(filePath);

        if (addedCount == -1) {

            System.out.println(
                    "The file could not be opened.");

            System.out.println(
                    "Please check that the file PATH and file TYPE are correct."
                            + "is correct.");

        } else if (inventoryManagementSystem.wasLastFileEmpty()) {

            System.out.println("File processing complete.");
            System.out.println("Document contained no entries.");

        } else {

            System.out.println("File processing complete.");
            System.out.println("Items successfully added: " + addedCount);

            int skippedCount =
                    inventoryManagementSystem.getLastSkippedLineCount();

            if (skippedCount > 0) {
                System.out.println(
                        "Lines skipped due to invalid entries: "
                                + skippedCount);
            }
        }

        return addedCount;
    }

    /**
     * Method: removeProduct
     * Purpose: Removes the product with the supplied identifier and reports if removal succeeded.
     * Arguments: inventoryManagementSystem, scanner.
     * Returns: boolean.
     */
    private boolean removeProduct(
            IMS inventoryManagementSystem,
            Scanner scanner) {

        System.out.println(
                "\n========== Remove Item ==========");

        String productId =
                getProductId(scanner);

        Product product =
                inventoryManagementSystem
                        .findProduct(productId);

        if (product == null) {

            System.out.println(
                    "No product was found with that ID.");

            return false;
        }

        System.out.println(
                displaySingleProduct(product));

        System.out.print(
                "\nRemove this product? (Y/N): ");

        String confirmation =
                scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")
                && !confirmation.equalsIgnoreCase("YES")) {

            System.out.println(
                    "Product was not removed.");

            return false;
        }


        boolean removed =
                inventoryManagementSystem
                        .removeProduct(productId);

        if (removed) {

            System.out.println(
                    "Product successfully removed.");

        } else {

            System.out.println(
                    "Product could not be removed.");
        }

        return removed;
    }

    /**
     * Method: searchProduct
     * Purpose: Searches inventory for a product and displays its details when found.
     * Arguments: inventoryManagementSystem, scanner.
     * Returns: Product.
     */
    private Product searchProduct(
            IMS inventoryManagementSystem,
            Scanner scanner) {

        System.out.println(
                "\n========== Search Item ==========");

        String productId =
                getProductId(scanner);

        Product product =
                inventoryManagementSystem
                        .findProduct(productId);

        if (product == null) {

            System.out.println(
                    "No product was found with that ID.");

        } else {

            System.out.println(
                    "\nProduct found:");

            System.out.println(
                    displaySingleProduct(product));
        }

        return product;
    }

    /**
     * Method: modifyProduct
     * Purpose: Updates an existing product while preventing duplicate product
     * names regardless of capitalization.
     * Arguments: inventoryManagementSystem, scanner.
     * Returns: boolean.
     */
    private boolean modifyProduct(
            IMS inventoryManagementSystem,
            Scanner scanner) {

        System.out.println("\n========== Modify Item ==========");

        String productId = getProductId(scanner);

        Product product =
                inventoryManagementSystem.findProduct(productId);

        if (product == null) {
            System.out.println(
                    "No product was found with that ID.");
            return false;
        }

        System.out.println("\nCurrent information:");
        System.out.println(displaySingleProduct(product));

        System.out.println(
                "\nPress Enter to keep the current value.");

        System.out.print(
                "Product name [" + product.getProductName() + "]: ");

        String productName = scanner.nextLine().trim();

        if (productName.isEmpty()) {
            productName = product.getProductName();
        }

        // Reject a name already used by another item,
        // regardless of capitalization.
        if (!productName.equalsIgnoreCase(product.getProductName())
                && inventoryManagementSystem.containsProductName(productName)) {

            System.out.println(
                    "Item has already been added to the list.");

            System.out.println(
                    "Select option '5. Modify Item' to update quantity or price listing.");

            return false;
        }

        String category =
                getModifiedCategory(scanner, product.getCategory());

        String supplier =
                getModifiedSupplier(scanner, product.getSupplier());

        int unitQuantity =
                getModifiedQuantity(scanner, product.getUnitQuantity());

        BigDecimal unitPrice =
                getModifiedPrice(scanner, product.getUnitPrice());

        try {

            boolean modified =
                    inventoryManagementSystem.modifyProduct(
                            productId,
                            productName,
                            category,
                            supplier,
                            unitQuantity,
                            unitPrice);

            if (modified) {
                System.out.println("Product successfully updated.");
            } else {
                System.out.println("Product could not be changed.");
            }

            return modified;

        } catch (IllegalArgumentException exception) {

            System.out.println(
                    "Invalid information: " + exception.getMessage());

            return false;
        }
    }

    /**
     * Method: viewProducts
     * Purpose: Displays all inventory products and returns the displayed text.
     * Arguments: inventoryManagementSystem.
     * Returns: String.
     */
    private String viewProducts(
            IMS inventoryManagementSystem) {

        System.out.println(
                "\n========== Inventory ==========");

        String inventory =
                inventoryManagementSystem
                        .getProductsDisplay();

        System.out.println(inventory);

        return inventory;
    }

    /**
     * Method: viewInventoryValue
     * Purpose: Calculates/displays the total inventory value and returns the value.
     * Arguments: inventoryManagementSystem.
     * Returns: BigDecimal.
     */
    private BigDecimal viewInventoryValue(
            IMS inventoryManagementSystem) {

        System.out.println(
                "\n========== Total Inventory Value ==========");

        BigDecimal totalValue =
                inventoryManagementSystem
                        .getTotalInventoryValue();

        System.out.println(
                "$"
                        + totalValue.setScale(2)
                        .toPlainString());

        return totalValue;
    }

    /**
     * Method: displaySingleProduct
     * Purpose: Formats and displays one product’s details, returning the formatted text.
     * Arguments: product.
     * Returns: String.
     */
    private String displaySingleProduct(
            Product product) {

        return "Product Name: "
                + product.getProductName()
                + "\nCategory: "
                + product.getCategory()
                + "\nSupplier: "
                + product.getSupplier()
                + "\nQuantity: "
                + product.getUnitQuantity()
                + "\nUnit Price: $"
                + product.getUnitPrice()
                .setScale(2)
                .toPlainString()
                + "\nProduct ID: "
                + product.getProductId();
    }

    /**
     * Method: getProductId
     * Purpose: Prompts for and returns a valid product identifier.
     * Arguments: scanner.
     * Returns: String.
     */
    private String getProductId(
            Scanner scanner) {

        ProductValidator validator =
                new ProductValidator();

        String productId = "";

        while (!validator.isValidProductId(productId)) {

            System.out.print(
                    "Enter the 7-digit product ID: ");

            productId =
                    scanner.nextLine().trim();

            if (!validator.isValidProductId(productId)) {

                System.out.println(
                        "Product ID must contain exactly 7 digits.");
            }
        }

        return productId;
    }

    /**
     * Method: getRequiredText
     * Purpose: Prompts for and returns non-empty text for the requested field.
     * Arguments: scanner, prompt.
     * Returns: String.
     */
    private String getRequiredText(
            Scanner scanner,
            String prompt) {

        ProductValidator validator =
                new ProductValidator();

        String userInput = "";

        while (!validator.isValidText(userInput)) {

            System.out.print(prompt);

            userInput =
                    scanner.nextLine().trim();

            if (!validator.isValidText(userInput)) {

                System.out.println(
                        "This field cannot be empty.");
            }
        }

        return userInput;
    }

    /**
     * Method: getRequiredCategory
     * Purpose: Prompts until a category containing at least one letter is entered.
     * Arguments: scanner.
     * Returns: String.
     */
    private String getRequiredCategory(Scanner scanner) {
        ProductValidator validator = new ProductValidator();
        String category = "";

        while (!validator.isValidCategory(category)) {
            System.out.print("Enter category: ");
            category = scanner.nextLine().trim();

            if (!validator.isValidCategory(category)) {
                System.out.println(
                        "Category must contain at least one letter. "
                                + "Please enter a valid category.");
            }
        }

        return category;
    }

    /**
     * Method: getRequiredSupplier
     * Purpose: Prompts until a supplier name containing at least one letter is entered.
     * Arguments: scanner.
     * Returns: String.
     */
    private String getRequiredSupplier(Scanner scanner) {
        ProductValidator validator = new ProductValidator();
        String supplier = "";

        while (!validator.isValidSupplier(supplier)) {
            System.out.print("Enter supplier: ");
            supplier = scanner.nextLine().trim();

            if (!validator.isValidSupplier(supplier)) {
                System.out.println(
                        "Supplier name must contain at least one letter. "
                                + "Please enter a valid supplier name.");
            }
        }

        return supplier;
    }

    /**
     * Method: getModifiedSupplier
     * Purpose: Prompts for a replacement supplier name or keeps the current value when unchanged.
     * Arguments: scanner, currentSupplier.
     * Returns: String.
     */
    private String getModifiedSupplier(
            Scanner scanner, String currentSupplier) {
        ProductValidator validator = new ProductValidator();

        while (true) {
            System.out.print("Supplier [" + currentSupplier + "]: ");
            String supplier = scanner.nextLine().trim();

            if (supplier.isEmpty()) {
                return currentSupplier;
            }

            if (validator.isValidSupplier(supplier)) {
                return supplier;
            }

            System.out.println(
                    "Supplier name must contain at least one letter. "
                            + "Please enter a valid supplier name.");
        }
    }

    /**
     * Method: getModifiedCategory
     * Purpose: Prompts for a replacement category or keeps the current value when unchanged.
     * Arguments: scanner, currentCategory.
     * Returns: String.
     */
    private String getModifiedCategory(
            Scanner scanner, String currentCategory) {
        ProductValidator validator = new ProductValidator();

        while (true) {
            System.out.print("Category [" + currentCategory + "]: ");
            String category = scanner.nextLine().trim();

            if (category.isEmpty()) {
                return currentCategory;
            }

            if (validator.isValidCategory(category)) {
                return category;
            }

            System.out.println(
                    "Category must contain at least one letter. "
                            + "Please enter a valid category.");
        }
    }

    /**
     * Method: getQuantity
     * Purpose: Prompts for and returns a valid non-negative whole-number quantity.
     * Arguments: scanner, prompt.
     * Returns: int.
     */
    private int getQuantity(
            Scanner scanner,
            String prompt) {

        ProductValidator validator =
                new ProductValidator();

        int unitQuantity = 0;

        boolean validQuantity = false;

        while (!validQuantity) {

            System.out.print(prompt);

            String quantityText =
                    scanner.nextLine().trim();

            try {

                unitQuantity =
                        Integer.parseInt(quantityText);

                if (validator.isValidQuantity(
                        unitQuantity)) {

                    validQuantity = true;

                } else {

                    System.out.println(
                            "Quantity must be a non-negative whole number (no decimals). ");
                }

            } catch (NumberFormatException exception) {

                System.out.println(
                        "Please enter a valid whole number.");
            }
        }

        return unitQuantity;
    }

    /**
     * Method: getPrice
     * Purpose: Prompts for and returns a valid non-negative unit price.
     * Arguments: scanner, prompt.
     * Returns: BigDecimal.
     */
    private BigDecimal getPrice(
            Scanner scanner,
            String prompt) {

        ProductValidator validator =
                new ProductValidator();

        BigDecimal unitPrice =
                BigDecimal.ZERO;

        boolean validPrice = false;

        while (!validPrice) {

            System.out.print(prompt);

            String priceText =
                    scanner.nextLine().trim();

            try {

                unitPrice =
                        new BigDecimal(priceText);

                if (validator.isValidPrice(
                        unitPrice)) {

                    validPrice = true;

                } else {

                    System.out.println(
                            "Price cannot be negative.");
                }

            } catch (NumberFormatException exception) {

                System.out.println(
                        "Please enter a valid price.");
            }
        }

        return unitPrice;
    }

    /**
     * Method: getModifiedQuantity
     * Purpose: Prompts for a replacement quantity or keeps the current quantity when unchanged.
     * Arguments: scanner, currentQuantity.
     * Returns: int.
     */
    private int getModifiedQuantity(
            Scanner scanner,
            int currentQuantity) {

        ProductValidator validator =
                new ProductValidator();

        int unitQuantity =
                currentQuantity;

        boolean validQuantity = false;

        while (!validQuantity) {

            System.out.print(
                    "Quantity ["
                            + currentQuantity
                            + "]: ");

            String quantityText =
                    scanner.nextLine().trim();

            if (quantityText.isEmpty()) {

                validQuantity = true;

            } else {

                try {

                    int newQuantity =
                            Integer.parseInt(quantityText);

                    if (validator.isValidQuantity(
                            newQuantity)) {

                        unitQuantity =
                                newQuantity;

                        validQuantity = true;

                    } else {

                        System.out.println(
                                "Quantity must be a non-negative whole number (no decimals). ");
                    }

                } catch (NumberFormatException exception) {

                    System.out.println(
                            "Please enter a valid whole number.");
                }
            }
        }

        return unitQuantity;
    }

    /**
     * Method: getModifiedPrice
     * Purpose: Prompts for a replacement unit price or keeps the current price when unchanged.
     * Arguments: scanner, currentPrice.
     * Returns: BigDecimal.
     */
    private BigDecimal getModifiedPrice(
            Scanner scanner,
            BigDecimal currentPrice) {

        ProductValidator validator =
                new ProductValidator();

        BigDecimal unitPrice =
                currentPrice;

        boolean validPrice = false;

        while (!validPrice) {

            System.out.print(
                    "Unit price ["
                            + currentPrice
                            + "]: ");

            String priceText =
                    scanner.nextLine().trim();

            if (priceText.isEmpty()) {

                validPrice = true;

            } else {

                try {

                    BigDecimal newPrice =
                            new BigDecimal(priceText);

                    if (validator.isValidPrice(
                            newPrice)) {

                        unitPrice =
                                newPrice;

                        validPrice = true;

                    } else {

                        System.out.println(
                                "Price cannot be negative.");
                    }

                } catch (NumberFormatException exception) {

                    System.out.println(
                            "Please enter a valid price.");
                }
            }
        }

        return unitPrice;
    }
}
