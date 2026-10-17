/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * Date: October 17, 2026
 * Class: Product
 * Purpose: Represents one inventory item and stores its identifying details:
 * category, supplier, quantity, and unit price. Values are validated before storage.
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
     * Method: Product
     * Purpose: Initializes a new instance of this class.
     * Arguments: productId, productName, category, supplier, unitQuantity, unitPrice.
     * Returns: none (void method).
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
                        "Product name");

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
     * Method: setProductId
     * Purpose: Validates and updates the product identifier.
     * Arguments: productId.
     * Returns: none (void method).
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
     * Method: setProductName
     * Purpose: Validates and updates the product name.
     * Arguments: productName.
     * Returns: none (void method).
     */
    public void setProductName(
            String productName) {

        ProductValidator validator =
                new ProductValidator();

        this.productName =
                validator.validateText(
                        productName,
                        "Product name");
    }

    /**
     * Method: setCategory
     * Purpose: Validates and updates the product category.
     * Arguments: category.
     * Returns: none (void method).
     */
    public void setCategory(
            String category) {

        ProductValidator validator =
                new ProductValidator();

        this.category =
                validator.validateCategory(category);
    }

    /**
     * Method: setSupplier
     * Purpose: Validates and updates the supplier name.
     * Arguments: supplier.
     * Returns: none (void method).
     */
    public void setSupplier(
            String supplier) {

        ProductValidator validator =
                new ProductValidator();

        this.supplier =
                validator.validateSupplier(supplier);
    }

    /**
     * Method: setUnitQuantity
     * Purpose: Validates and updates the inventory quantity.
     * Arguments: unitQuantity.
     * Returns: none (void method).
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
     * Method: setUnitPrice
     * Purpose: Validates and updates the unit price.
     * Arguments: unitPrice.
     * Returns: none (void method).
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
     * Method: getProductId
     * Purpose: Prompts for and returns a valid product identifier.
     * Arguments: none.
     * Returns: String.
     */
    public String getProductId() {

        return productId;
    }

    /**
     * Method: getProductName
     * Purpose: Validates and updates the product name.
     * Arguments: none.
     * Returns: String.
     */
    public String getProductName() {

        return productName;
    }

    /**
     * Method: getCategory
     * Purpose: Validates and updates the product category.
     * Arguments: none.
     * Returns: String.
     */
    public String getCategory() {

        return category;
    }

    /**
     * Method: getSupplier
     * Purpose: Validates and updates the supplier name.
     * Arguments: none.
     * Returns: String.
     */
    public String getSupplier() {

        return supplier;
    }

    /**
     * Method: getUnitQuantity
     * Purpose: Validates and updates the inventory quantity.
     * Arguments: none.
     * Returns: int.
     */
    public int getUnitQuantity() {

        return unitQuantity;
    }

    /**
     * Method: getUnitPrice
     * Purpose: Validates and updates the unit price.
     * Arguments: none.
     * Returns: BigDecimal.
     */
    public BigDecimal getUnitPrice() {

        return unitPrice;
    }
}
