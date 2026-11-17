package IMS;
/**
 @authorKelly Wilburn
  * Course: CEN 3024 - Software Development 1
  * @since November 16, 2026
 * Class: IMS.Product
 * Represents a single inventory record containing its identifier, name, category, supplier, quantity, and unit price. Uses IMS.ProductValidator to enforce field requirements before storing values; IMS.DatabaseManager persists and retrieves IMS.Product objects.
 */

import java.math.BigDecimal;

public class Product {

    private String productId;

    private String productName;

    private String category;

    private String supplier;

    private int unitQuantity;

    private BigDecimal unitPrice;

    /**
     * Creates a product and validates all supplied inventory fields before storing them.
     * @param productId seven-digit product identifier
     * @param productName name of the inventory product
     * @param category product category, containing at least one letter
     * @param supplier supplier name, containing at least one letter
     * @param unitQuantity nonnegative number of units in stock
     * @param unitPrice nonnegative price per unit
     * @throws IllegalArgumentException if any supplied field fails validation
     */
    public Product(

            String productId,

            String productName,

            String category,

            String supplier,

            int unitQuantity,

            BigDecimal unitPrice) {

        ProductValidator validator =

                new ProductValidator();

        this.productId =

                validator.validateProductId(

                        productId);

        this.productName =

                validator.validateText(

                        productName,

                        "IMS.Product name");

        this.category =

                validator.validateCategory(category);

        this.supplier =

                validator.validateSupplier(supplier);

        this.unitQuantity =

                validator.validateQuantity(

                        unitQuantity);

        this.unitPrice =

                validator.validatePrice(

                        unitPrice);

    }

    /**
     * Validates and updates the product identifier.
     * @param productId seven-digit product identifier
     * @throws IllegalArgumentException if any supplied field fails validation
     */
    public void setProductId(

            String productId) {

        ProductValidator validator =

                new ProductValidator();

        this.productId =

                validator.validateProductId(

                        productId);

    }

    /**
     * Validates and updates the product name.
     * @param productName name of the inventory product
     * @throws IllegalArgumentException if any supplied field fails validation
     */
    public void setProductName(

            String productName) {

        ProductValidator validator =

                new ProductValidator();

        this.productName =

                validator.validateText(

                        productName,

                        "IMS.Product name");

    }

    /**
     * Validates and updates the category.
     * @param category product category, containing at least one letter
     * @throws IllegalArgumentException if any supplied field fails validation
     */
    public void setCategory(

            String category) {

        ProductValidator validator =

                new ProductValidator();

        this.category =

                validator.validateCategory(category);

    }

    /**
     * Validates and updates the supplier name.
     * @param supplier supplier name, containing at least one letter
     * @throws IllegalArgumentException if any supplied field fails validation
     */
    public void setSupplier(

            String supplier) {

        ProductValidator validator =

                new ProductValidator();

        this.supplier =

                validator.validateSupplier(supplier);

    }

    /**
     * Validates and updates the quantity in stock.
     * @param unitQuantity nonnegative number of units in stock
     * @throws IllegalArgumentException if any supplied field fails validation
     */
    public void setUnitQuantity(

            int unitQuantity) {

        ProductValidator validator =

                new ProductValidator();

        this.unitQuantity =

                validator.validateQuantity(

                        unitQuantity);

    }

    /**
     * Validates and updates the unit price.
     * @param unitPrice nonnegative price per unit
     * @throws IllegalArgumentException if any supplied field fails validation
     */
    public void setUnitPrice(

            BigDecimal unitPrice) {

        ProductValidator validator =

                new ProductValidator();

        this.unitPrice =

                validator.validatePrice(

                        unitPrice);

    }

    /**
     * Returns the stored product identifier.
     * @return current product identifier
     */
    public String getProductId() {

        return productId;

    }

    /**
     * Returns the stored product name.
     * @return current product name
     */
    public String getProductName() {

        return productName;

    }

    /**
     * Returns the stored category.
     * @return current category
     */
    public String getCategory() {

        return category;

    }

    /**
     * Returns the stored supplier name.
     * @return current supplier name
     */
    public String getSupplier() {

        return supplier;

    }

    /**
     * Returns the stored quantity in stock.
     * @return current quantity in stock
     */
    public int getUnitQuantity() {

        return unitQuantity;

    }

    /**
     * Returns the stored unit price.
     * @return current unit price
     */
    public BigDecimal getUnitPrice() {

        return unitPrice;

    }

}
