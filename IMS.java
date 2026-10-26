/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * October 17, 2026
 * IMS.java
 *
 * This class manages the inventory collection for the Inventory Management
 * System. It supports adding, importing, searching, modifying, removing,
 * sorting, and displaying products, as well as calculating total inventory
 * value. Duplicate product IDs and product names are rejected, with product
 * names compared without regard to capitalization.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class IMS {

    private ArrayList<Product> products;
    private int lastSkippedLineCount;
    private boolean lastFileEmpty;

    /**
     * Initializes an empty inventory and resets file-import status.
     * Constructor parameters: none.
     * Return: none.
     */
    public IMS() {
        products = new ArrayList<>();
        lastSkippedLineCount = 0;
        lastFileEmpty = false;
    }

    /**
     * Method: addProduct
     * Purpose: Adds a product only when its ID and name are unique.
     * Parameters: product - the Product object to add.
     * Return: boolean - true if added; false if null or a duplicate.
     */
    public boolean addProduct(Product product) {

        if (product == null) {
            return false;
        }

        if (findProduct(product.getProductId()) != null) {
            return false;
        }

        if (containsProductName(product.getProductName())) {
            return false;
        }

        products.add(product);
        return true;
    }

    /**
     * Method: containsProductName
     * Purpose: Determines whether a product name already exists in inventory,
     * ignoring capitalization.
     * Parameters: productName - the name to search for.
     * Return: boolean - true if the name exists; otherwise false.
     */
    public boolean containsProductName(String productName) {

        if (productName == null) {
            return false;
        }

        for (Product product : products) {
            if (product.getProductName().equalsIgnoreCase(productName.trim())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method: loadProducts
     * Purpose: Reads product records from a text file, validates each record,
     * adds valid unique products, and tracks invalid or duplicate entries.
     * Parameters: filePath - the path to the text file.
     * Return: int - number of products added, or -1 if the file cannot be opened.
     */
    public int loadProducts(String filePath) {

        int addedCount = 0;
        int skippedCount = 0;
        boolean foundEntry = false;

        lastSkippedLineCount = 0;
        lastFileEmpty = false;

        if (filePath == null
                || !filePath.toLowerCase().endsWith(".txt")) {
            return -1;
        }

        try (Scanner fileScanner = new Scanner(new File(filePath))) {

            while (fileScanner.hasNextLine()) {

                String line = fileScanner.nextLine();

                if (line.trim().isEmpty()) {
                    continue;
                }

                foundEntry = true;

                Product product = createProductFromLine(line);

                if (product == null) {
                    skippedCount++;
                } else if (addProduct(product)) {
                    addedCount++;
                } else {
                    skippedCount++;
                }
            }

        } catch (FileNotFoundException exception) {
            return -1;
        }

        lastSkippedLineCount = skippedCount;
        lastFileEmpty = !foundEntry;

        return addedCount;
    }

    /**
     * Method: getLastSkippedLineCount
     * Purpose: Returns the number of invalid or duplicate file entries skipped
     * during the most recent import.
     * Parameters: none.
     * Return: int - the number of skipped entries.
     */
    public int getLastSkippedLineCount() {
        return lastSkippedLineCount;
    }

    /**
     * Method: wasLastFileEmpty
     * Purpose: Reports whether the most recently imported file contained
     * any non-empty lines.
     * Parameters: none.
     * Return: boolean - true if no non-empty lines were found.
     */
    public boolean wasLastFileEmpty() {
        return lastFileEmpty;
    }

    /**
     * Method: createProductFromLine
     * Purpose: Parses and validates one text-file record before creating
     * a Product object.
     * Parameters: line - a record containing six hyphen-separated fields.
     * Return: Product - the validated product, or null if the record is invalid.
     */
    private Product createProductFromLine(String line) {

        ProductValidator validator = new ProductValidator();

        String[] productParts = line.split("-", -1);

        if (productParts.length != 6) {
            return null;
        }

        String productId = productParts[0].trim();
        String productName = productParts[1].trim();
        String category = productParts[2].trim();
        String supplier = productParts[3].trim();
        String quantityText = productParts[4].trim();
        String priceText = productParts[5].trim();

        if (!validator.isValidProductId(productId)
                || !validator.isValidText(productName)
                || !validator.isValidCategory(category)
                || !validator.isValidSupplier(supplier)) {
            return null;
        }

        int unitQuantity;

        try {
            unitQuantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException exception) {
            return null;
        }

        if (!validator.isValidQuantity(unitQuantity)) {
            return null;
        }

        BigDecimal unitPrice;

        try {
            unitPrice = new BigDecimal(priceText);
        } catch (NumberFormatException exception) {
            return null;
        }

        if (!validator.isValidPrice(unitPrice)) {
            return null;
        }

        try {
            return new Product(
                    productId,
                    productName,
                    category,
                    supplier,
                    unitQuantity,
                    unitPrice);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * Method: findProduct
     * Purpose: Finds a product by its exact product ID.
     * Parameters: productId - the ID to search for.
     * Return: Product - the matching product, or null if not found.
     */
    public Product findProduct(String productId) {

        if (productId == null) {
            return null;
        }

        for (Product product : products) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }

        return null;
    }

    /**
     * Method: removeProduct
     * Purpose: Removes a product from inventory using its product ID.
     * Parameters: productId - the ID of the product to remove.
     * Return: boolean - true if removed; otherwise false.
     */
    public boolean removeProduct(String productId) {

        Product product = findProduct(productId);

        if (product != null) {
            return products.remove(product);
        }

        return false;
    }

    /**
     * Method: modifyProduct
     * Purpose: Updates an existing product's details while preventing its
     * name from duplicating another product's name, ignoring capitalization.
     * Parameters: productId - ID of the product to modify;
     * productName - updated name;
     * category - updated category;
     * supplier - updated supplier;
     * unitQuantity - updated quantity;
     * unitPrice - updated unit price.
     * Return: boolean - true if updated; false if the product does not exist
     * or its proposed name duplicates another product.
     */
    public boolean modifyProduct(
            String productId,
            String productName,
            String category,
            String supplier,
            int unitQuantity,
            BigDecimal unitPrice) {

        Product product = findProduct(productId);

        if (product == null || productName == null) {
            return false;
        }

        String trimmedName = productName.trim();

        for (Product existingProduct : products) {

            if (existingProduct != product
                    && existingProduct.getProductName()
                    .equalsIgnoreCase(trimmedName)) {

                return false;
            }
        }

        try {
            product.setProductName(trimmedName);
            product.setCategory(category);
            product.setSupplier(supplier);
            product.setUnitQuantity(unitQuantity);
            product.setUnitPrice(unitPrice);

            return true;

        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /**
     * Method: getProductsAlphabetically
     * Purpose: Returns a copy of the inventory sorted by product name.
     * Parameters: none.
     * Return: ArrayList<Product> - products sorted alphabetically,
     * ignoring capitalization.
     */
    public ArrayList<Product> getProductsAlphabetically() {

        ArrayList<Product> sortedProducts = new ArrayList<>(products);

        sortedProducts.sort(
                Comparator.comparing(
                        Product::getProductName,
                        String.CASE_INSENSITIVE_ORDER));

        return sortedProducts;
    }

    /**
     * Method: getProductsDisplay
     * Purpose: Creates a formatted listing of all products in inventory.
     * Parameters: none.
     * Return: String - formatted inventory information or an empty-inventory message.
     */
    public String getProductsDisplay() {

        ArrayList<Product> sortedProducts = getProductsAlphabetically();

        if (sortedProducts.isEmpty()) {
            return "There are currently no items in inventory.";
        }

        StringBuilder output = new StringBuilder();

        int itemNumber = 1;

        for (Product product : sortedProducts) {

            output.append("\n")
                    .append(itemNumber)
                    .append(". ")
                    .append(product.getProductName())
                    .append("  ")
                    .append(formatCurrency(product.getUnitPrice()))
                    .append("\n");

            output.append("    ")
                    .append(product.getUnitQuantity())
                    .append("\n");

            output.append("    ")
                    .append(product.getSupplier())
                    .append("\n");

            output.append("    ")
                    .append(product.getCategory())
                    .append("\n");

            output.append("    ")
                    .append(product.getProductId())
                    .append("\n");

            itemNumber++;
        }

        return output.toString();
    }

    /**
     * Method: getTotalInventoryValue
     * Purpose: Calculates the total value of all inventory quantities
     * multiplied by their corresponding unit prices.
     * Parameters: none.
     * Return: BigDecimal - the total inventory value.
     */
    public BigDecimal getTotalInventoryValue() {

        BigDecimal totalInventoryValue = BigDecimal.ZERO;

        for (Product product : products) {

            BigDecimal productValue = product.getUnitPrice()
                    .multiply(BigDecimal.valueOf(product.getUnitQuantity()));

            totalInventoryValue = totalInventoryValue.add(productValue);
        }

        return totalInventoryValue;
    }

    /**
     * Method: getProductCount
     * Purpose: Returns the number of products currently stored in inventory.
     * Parameters: none.
     * Return: int - the number of products.
     */
    public int getProductCount() {
        return products.size();
    }

    /**
     * Method: formatCurrency
     * Purpose: Formats a monetary amount with a dollar sign and two decimal places.
     * Parameters: amount - the monetary amount to format.
     * Return: String - the formatted currency value.
     */
    private String formatCurrency(BigDecimal amount) {

        return "$"
                + amount.setScale(2)
                .toPlainString();
    }
}