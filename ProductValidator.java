/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * Date: October 17, 2026
 * Class: ProductValidator
 * Purpose: Ensures the product information entered meets the inventory program’s validation requirements.
 * It checks product IDs, text fields, categories, suppliers, quantities, and prices, rejecting invalid
 * data and ensuring text fields are properly formatted.
 */
import java.math.BigDecimal;

public class ProductValidator {

    /**
     * Method: isValidProductId
     * Purpose: Checks if the identifier contains exactly seven digits.
     * Arguments: productId.
     * Returns: boolean.
     */
    public boolean isValidProductId(
            String productId) {

        if (productId == null) {

            return false;
        }

        return productId.matches("\\d{7}");
    }

    /**
     * Method: isValidText
     * Purpose: Checks whether a text value is non-null and not blank.
     * Arguments: text.
     * Returns: boolean.
     */
    public boolean isValidText(
            String text) {

        if (text == null) {

            return false;
        }

        return !text.trim().isEmpty();
    }

    /**
     * Method: isValidCategory
     * Purpose: Checks if a category contains at least one letter.
     * Arguments: category.
     * Returns: boolean.
     */
    public boolean isValidCategory(String category) {
        return category != null
                && category.matches("(?s).*\\p{L}.*");
    }

    /**
     * Method: isValidSupplier
     * Purpose: Checks if a supplier name contains at least one letter.
     * Arguments: supplier.
     * Returns: boolean.
     */
    public boolean isValidSupplier(String supplier) {
        return supplier != null
                && supplier.matches("(?s).*\\p{L}.*");
    }

    /**
     * Method: isValidQuantity
     * Purpose: Checks whether the quantity is non-negative.
     * Arguments: unitQuantity.
     * Returns: boolean.
     */
    public boolean isValidQuantity(
            int unitQuantity) {

        return unitQuantity >= 0;
    }

    /**
     * Method: isValidPrice
     * Purpose: Checks whether a price is non-null and non-negative.
     * Arguments: unitPrice.
     * Returns: boolean.
     */
    public boolean isValidPrice(
            BigDecimal unitPrice) {

        if (unitPrice == null) {

            return false;
        }

        return unitPrice.compareTo(
                BigDecimal.ZERO) >= 0;
    }

    /**
     * Method: validateProductId
     * Purpose: Validates a product identifier and returns it, throwing an exception if invalid.
     * Arguments: productId.
     * Returns: String.
     */
    public String validateProductId(
            String productId) {

        if (!isValidProductId(productId)) {

            throw new IllegalArgumentException(
                    "Product ID must contain exactly 7 digits.");
        }

        return productId;
    }

    /**
     * Method: validateText
     * Purpose: Validates non-empty text and returns its trimmed form.
     * Arguments: text, fieldName.
     * Returns: String.
     */
    public String validateText(
            String text,
            String fieldName) {

        if (!isValidText(text)) {

            throw new IllegalArgumentException(
                    fieldName
                            + " cannot be empty.");
        }

        return text.trim();
    }

    /**
     * Method: validateCategory
     * Purpose: Validates a category and returns its trimmed form.
     * Arguments: category.
     * Returns: String.
     */
    public String validateCategory(String category) {
        if (!isValidCategory(category)) {
            throw new IllegalArgumentException(
                    "Category must contain at least one letter.");
        }
        return category.trim();
    }

    /**
     * Method: validateSupplier
     * Purpose: Validates a supplier name and returns its trimmed form.
     * Arguments: supplier.
     * Returns: String.
     */
    public String validateSupplier(String supplier) {
        if (!isValidSupplier(supplier)) {
            throw new IllegalArgumentException(
                    "Supplier name must contain at least one letter.");
        }
        return supplier.trim();
    }

    /**
     * Method: validateQuantity
     * Purpose: Validates a quantity and returns it, throwing an exception if negative.
     * Arguments: unitQuantity.
     * Returns: int.
     */
    public int validateQuantity(
            int unitQuantity) {

        if (!isValidQuantity(unitQuantity)) {

            throw new IllegalArgumentException(
                    "Quantity cannot be negative.");
        }

        return unitQuantity;
    }

    /**
     * Method: validatePrice
     * Purpose: Validates a price and returns it, throwing an exception if negative or null.
     * Arguments: unitPrice.
     * Returns: BigDecimal.
     */
    public BigDecimal validatePrice(
            BigDecimal unitPrice) {

        if (!isValidPrice(unitPrice)) {

            throw new IllegalArgumentException(
                    "Price cannot be negative.");
        }

        return unitPrice;
    }
}
