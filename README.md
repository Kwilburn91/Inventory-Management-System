# Inventory Management System (IMS)

**Name:** Kelly Wilburn  
**Course:** Software Development I CEN-3024C

## 1. Project Description

This project is a Java console-based Inventory Management System (IMS). The application allows users to manage a collection of business inventory products through a simple, menu-driven interface.

### Users can:

- Add a new product manually.
- Import multiple products from a text file.
- Remove an existing product by ID.
- Search for a product by ID.
- Modify an existing product's information.
- Display all stored products in alphabetical order by product name.
- Calculate and display the total inventory value.
- Exit the application.

The application uses an `ArrayList<Product>` to store and manage `Product` objects. Each product contains a unique 7-digit product ID, product name, category, supplier, quantity, and unit price.

## 2. Important Programming Concepts and Tools

The project demonstrates several important Java programming concepts:

- **Object-Oriented Programming (OOP):** The application is organized into multiple classes, including `Main`, `IMS`, `Product`, and `ProductValidator`.
- **Encapsulation:** The `Product` class declares its fields as `private` and provides getter and setter methods to access and modify product information.
- **Classes and Objects:** `Product` objects represent individual inventory items.
- **Collections and ArrayLists:** An `ArrayList<Product>` stores and manages the inventory collection.
- **Methods:** Separate methods handle adding, importing, searching, modifying, removing, displaying, validating, and calculating inventory values.
- **Control Flow:** `if/else`, `switch`, `while`, and `for` statements control application behavior and menu selection.
- **Input/Output:** Java's `Scanner` class collects keyboard and file input, while `System.out.print()` and `System.out.println()` display information.
- **File Input:** The `File` and `Scanner` classes read product records from a text file.
- **Exception Handling:** `try/catch` blocks handle invalid numeric input, file errors, and validation exceptions.
- **Input Validation:** The `ProductValidator` class checks product information before it is stored or modified.
- **Regular Expressions:** Regular expressions validate product IDs, categories, and supplier names.
- **String Manipulation:** Methods such as `trim()`, `split()`, `substring()`, and `matches()` process user input and file records.
- **Duplicate Detection:** The system searches for existing product IDs before adding new products.
- **Sorting:** `Comparator.comparing()` and `String.CASE_INSENSITIVE_ORDER` sort products alphabetically by name.
- **Decimal Arithmetic:** `BigDecimal` handles unit prices and inventory value calculations to reduce floating-point precision errors.
- **Inventory Calculations:** The system multiplies each product's unit price by its quantity and adds the results to calculate total inventory value.

## 3. Expected Inputs and Outputs

### Inputs

The application accepts the following menu selections:

1. Add Item Manually
2. Add Item(s) From a Text File
3. Remove Item
4. Search Item
5. Modify Item
6. Display All Items
7. Display Total Inventory Value
8. Exit

When manually adding a product, the user enters:

- 7-digit product ID
- Product name
- Category
- Supplier
- Quantity as a non-negative whole number
- Unit price as a non-negative decimal number

When modifying a product, the user enters the product ID and may update its name, category, supplier, quantity, or unit price. Pressing Enter without entering a replacement value keeps the existing value.

When removing or searching for a product, the user enters the product's 7-digit ID. Removal requires confirmation by entering `Y` or `YES`.

When importing products from a text file, the user enters a file path.

The expected text file format is:

```text
ProductID-ProductName-Category-Supplier-Quantity-UnitPrice
```

Example:

```text
1234567-Wireless Keyboard-Electronics-MicroCenter-42-29.99
```

Each record must contain six fields separated by hyphens. The unit price must not include a dollar sign.

### Outputs

The application displays messages depending on the selected operation:

- **Add Item Manually:** Confirms whether the product was successfully added.
- **Add Items From File:** Reports the number of successfully added products and the number of invalid or duplicate entries skipped.
- **Remove Item:** Displays the product's details, requests confirmation, and reports whether removal succeeded.
- **Search Item:** Displays the product's details when a matching ID is found.
- **Modify Item:** Displays the current product information and allows the user to update individual fields.
- **Display All Items:** Displays products alphabetically by name, including their price, quantity, supplier, category, and ID.
- **Display Total Inventory Value:** Shows the combined value of all inventory quantities at their respective unit prices, formatted as currency.
- **Empty Inventory:** Informs the user when no products are available for operations requiring existing items.
- **Duplicate Product ID:** Prevents a product from being added when its ID already exists.
- **Invalid Input:** Displays appropriate error messages when user input fails validation.
- **Invalid Menu Selection:** Requests a menu number between 1 and 8.
- **Invalid File:** Reports when the specified file cannot be opened.
- **Exit:** Displays a thank-you message and terminates the application.

## 4. Data Validation Rules

The application uses the `ProductValidator` class to ensure product information meets the required standards.

- **Product ID:** Must contain exactly 7 digits.
- **Product Name:** Cannot be null, empty, or blank.
- **Category:** Must contain at least one letter.
- **Supplier:** Must contain at least one letter.
- **Quantity:** Must be a non-negative whole number without decimals.
- **Unit Price:** Must be a valid, non-negative decimal number.
- **Duplicate IDs:** A product cannot be added if another product has the same ID.
- **File Records:** Each non-empty record must contain exactly six hyphen-separated fields with valid product information.
- **File Import Duplicates:** Existing product IDs and invalid records are skipped during import.
- **Modified Values:** Updated product information must pass the same validation rules as newly added products.
- **Optional Updates:** Pressing Enter keeps the current value for the product name, category, supplier, quantity, or unit price.

The system repeatedly prompts users to correct invalid manually entered values. During file imports, invalid or duplicate records are skipped, allowing the program to continue processing other entries.
