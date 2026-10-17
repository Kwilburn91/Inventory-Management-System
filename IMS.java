/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * Date: October 17, 2026
 * Class: IMS
 * Purpose: Manages the inventory collection and its operations, including:
 * adding, importing, searching, modifying, removing, sorting, and calculating
 * the value of product records.
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
     * Method: IMS
     * Purpose: Initializes a new instance of this class.
     * Arguments: none.
     * Returns: none (void method).
     */
    public IMS() {

        products =
                new ArrayList<>();
        lastSkippedLineCount = 0;
        lastFileEmpty = false;
    }

    /**
     * Method: addProduct
     * Purpose: Adds a product to inventory unless the input is null or its identifier already exists.
     * Arguments: product.
     * Returns: boolean.
     */
    public boolean addProduct(
            Product product) {

        if (product == null) {

            return false;
        }

        Product existingProduct =
                findProduct(
                        product.getProductId());

        if (existingProduct != null) {

            return false;
        }

        products.add(product);

        return true;
    }

    /**
     * Method: loadProducts
     * Purpose: Reads product records from a text file, adds valid records, and tracks skipped entries.
     * Arguments: filePath.
     * Returns: int.
     */
    public int loadProducts(String filePath) {
        int addedCount = 0;
        int skippedCount = 0;
        boolean foundEntry = false;
        lastSkippedLineCount = 0;
        lastFileEmpty = false;

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
     * Purpose: Returns the number of invalid or duplicate non-empty file entries skipped during the last import.
     * Arguments: none.
     * Returns: int.
     */
    public int getLastSkippedLineCount() {
        return lastSkippedLineCount;
    }

    /**
     * Method: wasLastFileEmpty
     * Purpose: Reports whether the most recently processed file contained no non-empty entries.
     * Arguments: none.
     * Returns: boolean.
     */
    public boolean wasLastFileEmpty() {
        return lastFileEmpty;
    }

    /**
     * Method: createProductFromLine
     * Purpose: Parses and validates one file line, returning a Product or null when the line is invalid.
     * Arguments: line.
     * Returns: Product.
     */
    private Product createProductFromLine(
            String line) {

        ProductValidator validator =
                new ProductValidator();

        String[] productParts =
                line.split("-", -1);

        if (productParts.length != 6) {

            return null;
        }

        String productId =
                productParts[0].trim();

        String productName =
                productParts[1].trim();

        String category =
                productParts[2].trim();

        String supplier =
                productParts[3].trim();

        String quantityText =
                productParts[4].trim();

        String priceText =
                productParts[5].trim();

        if (!validator.isValidProductId(productId)) {

            return null;
        }

        if (!validator.isValidText(productName)) {

            return null;
        }

        if (!validator.isValidCategory(category)) {

            return null;
        }

        if (!validator.isValidSupplier(supplier)) {

            return null;
        }

        int unitQuantity;

        try {

            unitQuantity =
                    Integer.parseInt(quantityText);

        } catch (NumberFormatException exception) {

            return null;
        }

        if (!validator.isValidQuantity(
                unitQuantity)) {

            return null;
        }

        BigDecimal unitPrice;

        try {

            unitPrice =
                    new BigDecimal(priceText);

        } catch (NumberFormatException exception) {

            return null;
        }

        if (!validator.isValidPrice(unitPrice)) {

            return null;
        }

        return new Product(
                productId,
                productName,
                category,
                supplier,
                unitQuantity,
                unitPrice);
    }

    /**
     * Method: findProduct
     * Purpose: Finds and returns the product with the requested identifier, or null if no match exists.
     * Arguments: productId.
     * Returns: Product.
     */
    public Product findProduct(
            String productId) {

        for (Product product : products) {

            if (product.getProductId()
                    .equals(productId)) {

                return product;
            }
        }

        return null;
    }

    /**
     * Method: removeProduct
     * Purpose: Removes the product with the supplied identifier and reports whether the removal succeeded.
     * Arguments: productId.
     * Returns: boolean.
     */
    public boolean removeProduct(
            String productId) {

        Product product =
                findProduct(productId);

        if (product != null) {

            return products.remove(product);
        }

        return false;
    }

    /**
     * Method: modifyProduct
     * Purpose: Updates the fields modified of an existing product and reports whether it was found.
     * Arguments: productId, productName, category, supplier, unitQuantity, unitPrice.
     * Returns: boolean.
     */
    public boolean modifyProduct(
            String productId,
            String productName,
            String category,
            String supplier,
            int unitQuantity,
            BigDecimal unitPrice) {

        Product product =
                findProduct(productId);

        if (product == null) {

            return false;
        }

        product.setProductName(productName);

        product.setCategory(category);

        product.setSupplier(supplier);

        product.setUnitQuantity(unitQuantity);

        product.setUnitPrice(unitPrice);

        return true;
    }

    /**
     * Method: getProductsAlphabetically
     * Purpose: Returns a copy of the inventory products sorted alphabetically by product name.
     * Arguments: none.
     * Returns: ArrayList<Product>.
     */
    public ArrayList<Product>
    getProductsAlphabetically() {

        ArrayList<Product> sortedProducts =
                new ArrayList<>(products);

        sortedProducts.sort(
                Comparator.comparing(
                        Product::getProductName,
                        String.CASE_INSENSITIVE_ORDER));

        return sortedProducts;
    }

    /**
     * Method: getProductsDisplay
     * Purpose: Builds and returns a formatted display of all inventory products.
     * Arguments: none.
     * Returns: String.
     */
    public String getProductsDisplay() {

        ArrayList<Product> sortedProducts =
                getProductsAlphabetically();

        if (sortedProducts.isEmpty()) {

            return "There are currently no items in inventory.";
        }

        StringBuilder output =
                new StringBuilder();

        int itemNumber = 1;

        for (Product product : sortedProducts) {

            output.append("\n")
                    .append(itemNumber)
                    .append(". ")
                    .append(product.getProductName())
                    .append("  ")
                    .append(formatCurrency(
                            product.getUnitPrice()))
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
     * Purpose: Calculates and returns the combined value of all quantities at their unit prices.
     * Arguments: none.
     * Returns: BigDecimal.
     */
    public BigDecimal getTotalInventoryValue() {

        BigDecimal totalInventoryValue =
                BigDecimal.ZERO;

        for (Product product : products) {

            BigDecimal productValue =
                    product.getUnitPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            product.getUnitQuantity()));

            totalInventoryValue =
                    totalInventoryValue.add(
                            productValue);
        }

        return totalInventoryValue;
    }

    /**
     * Method: getProductCount
     * Purpose: Returns the number of products currently in inventory.
     * Arguments: none.
     * Returns: int.
     */
    public int getProductCount() {

        return products.size();
    }

    /**
     * Method: formatCurrency
     * Purpose: Formats a decimal amount as a dollar string with two decimal places.
     * Arguments: amount.
     * Returns: String.
     */
    private String formatCurrency(
            BigDecimal amount) {

        return "$"
                + amount.setScale(2)
                .toPlainString();
    }
}
