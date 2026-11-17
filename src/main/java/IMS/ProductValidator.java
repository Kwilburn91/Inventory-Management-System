package IMS;
/**
 @authorKelly Wilburn
  * Course: CEN 3024 - Software Development 1
  * @since November 16, 2026
 * Class: IMS.ProductValidator
 * Provides reusable checks and validated values for inventory fields. IMS.Product and IMS.GUI use this class to enforce consistent identifier, text, quantity, and price requirements before database operations.
 */

import java.math.BigDecimal;

public class ProductValidator {

    /**
     * Checks whether the identifier contains exactly seven digits.
     * @param productId seven-digit product identifier
     * @return true if the value is valid; otherwise false
     */
    public boolean isValidProductId(

            String productId) {

        if (productId == null) {

            return false;

        }

        return productId.matches("\\d{7}");

    }

    /**
     * Checks whether the text is not null or blank.
     * @param text text to validate
     * @return true if the value is valid; otherwise false
     */
    public boolean isValidText(

            String text) {

        if (text == null) {

            return false;

        }

        return !text.trim().isEmpty();

    }

    /**
     * Checks whether the category contains at least one letter.
     * @param category product category, containing at least one letter
     * @return true if the value is valid; otherwise false
     */
    public boolean isValidCategory(String category) {

        return category != null

                && category.matches("(?s).*\\p{L}.*");

    }

    /**
     * Checks whether the supplier contains at least one letter.
     * @param supplier supplier name, containing at least one letter
     * @return true if the value is valid; otherwise false
     */
    public boolean isValidSupplier(String supplier) {

        return supplier != null

                && supplier.matches("(?s).*\\p{L}.*");

    }

    /**
     * Checks whether the quantity is nonnegative.
     * @param unitQuantity nonnegative number of units in stock
     * @return true if the value is valid; otherwise false
     */
    public boolean isValidQuantity(

            int unitQuantity) {

        return unitQuantity >= 0;

    }

    /**
     * Checks whether the price is nonnull and nonnegative.
     * @param unitPrice nonnegative price per unit
     * @return true if the value is valid; otherwise false
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
     * Checks that the identifier contains exactly seven digits and returns it.
     * @param productId seven-digit product identifier
     * @return validated productid value
     * @throws IllegalArgumentException if any supplied field fails validation
     */
    public String validateProductId(

            String productId) {

        if (!isValidProductId(productId)) {

            throw new IllegalArgumentException(

                    "IMS.Product ID must contain exactly 7 digits.");

        }

        return productId;

    }

    /**
     * Checks that text is nonblank and returns its trimmed value.
     * @param text text to validate
     * @param fieldName name of the field used in validation errors
     * @return trimmed text value
     * @throws IllegalArgumentException if any supplied field fails validation
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
     * Checks that the category contains a letter and returns its trimmed value.
     * @param category product category, containing at least one letter
     * @return trimmed category value
     * @throws IllegalArgumentException if any supplied field fails validation
     */
    public String validateCategory(String category) {

        if (!isValidCategory(category)) {

            throw new IllegalArgumentException(

                    "Category must contain at least one letter.");

        }

        return category.trim();

    }

    /**
     * Checks that the supplier contains a letter and returns its trimmed value.
     * @param supplier supplier name, containing at least one letter
     * @return trimmed supplier value
     * @throws IllegalArgumentException if any supplied field fails validation
     */
    public String validateSupplier(String supplier) {

        if (!isValidSupplier(supplier)) {

            throw new IllegalArgumentException(

                    "Supplier name must contain at least one letter.");

        }

        return supplier.trim();

    }

    /**
     * Checks that quantity is nonnegative and returns it.
     * @param unitQuantity nonnegative number of units in stock
     * @return validated quantity value
     * @throws IllegalArgumentException if any supplied field fails validation
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
     * Checks that price is nonnull and nonnegative and returns it.
     * @param unitPrice nonnegative price per unit
     * @return validated price value
     * @throws IllegalArgumentException if any supplied field fails validation
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
