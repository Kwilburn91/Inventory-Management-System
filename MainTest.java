
/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * October 26, 2026
 * MainTest.java
 *
 * Purpose: Tests the console interface of the Inventory Management System.
 * Verifies menu display, invalid menu choices, exit behavior, manual product
 * creation, input validation, file-path handling, empty inventory display,
 * and the custom inventory valuation menu option.
 */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @TempDir
    Path tempDir;

    /**
     * Method: runApplicationWithInput
     * Purpose: Runs the application with simulated keyboard input and
     * captures console output for assertions.
     * Parameters: input - simulated user input.
     * Return: String - the output printed by the application.
     */
    private String runApplicationWithInput(String input) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        ByteArrayOutputStream capturedOutput =
                new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream(
                    input.getBytes(StandardCharsets.UTF_8)));

            System.setOut(new PrintStream(
                    capturedOutput,
                    true,
                    StandardCharsets.UTF_8));

            Main application = new Main();

            String result = application.run();

            assertEquals(
                    "Inventory Management System closed.",
                    result);

            return capturedOutput.toString(StandardCharsets.UTF_8);

        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    // =========================================================
    // Test 1: Display a Menu
    // =========================================================

    /**
     * Method: testMenuDisplaysAllOptions
     * Purpose: Verifies that all eight inventory menu options are displayed.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testMenuDisplaysAllOptions() {
        String output = runApplicationWithInput("8\n");

        assertTrue(output.contains("1. Add Item Manually"));
        assertTrue(output.contains("2. Add Item(s) From A Text File"));
        assertTrue(output.contains("3. Remove Item"));
        assertTrue(output.contains("4. Search Item"));
        assertTrue(output.contains("5. Modify Item"));
        assertTrue(output.contains("6. Display All Items"));
        assertTrue(output.contains("7. Display Total Inventory Value"));
        assertTrue(output.contains("8. Exit"));
    }

    /**
     * Method: testInvalidMenuInput
     * Purpose: Verifies that non-numeric and out-of-range choices are
     * rejected without terminating the application.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testInvalidMenuInput() {
        String output = runApplicationWithInput(
                "abc\n"
                        + "9\n"
                        + "0\n"
                        + "8\n");

        assertTrue(output.contains("Invalid input."));
        assertTrue(output.contains("Invalid menu choice."));
        assertTrue(output.contains(
                "Please enter a number from 1 through 8."));
        assertTrue(output.contains(
                "Thank you for using the Inventory Management System."));
    }

    /**
     * Method: testApplicationExitsOnlyWhenExitIsSelected
     * Purpose: Verifies that the application continues after an invalid
     * selection and exits when option 8 is selected.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testApplicationExitsOnlyWhenExitIsSelected() {
        String output = runApplicationWithInput(
                "10\n"
                        + "8\n");

        assertTrue(output.contains("Invalid menu choice."));
        assertTrue(output.contains(
                "Thank you for using the Inventory Management System."));
    }

    // =========================================================
    // Test 2: Read Data
    // =========================================================

    /**
     * Method: testBlankFilePathIsRejected
     * Purpose: Verifies that a blank file path is rejected without
     * terminating the application.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testBlankFilePathIsRejected() {
        String output = runApplicationWithInput(
                "2\n"
                        + "\n"
                        + "8\n");

        assertTrue(output.contains("The file could not be opened."));
        assertTrue(output.contains(
                "Please check that the file type is a text file"));
        assertTrue(output.contains(
                "Thank you for using the Inventory Management System."));
    }

    /**
     * Method: testWrongFileExtensionIsRejected
     * Purpose: Verifies that a file path without the required .txt
     * extension is rejected.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testWrongFileExtensionIsRejected() {
        String output = runApplicationWithInput(
                "2\n"
                        + "inventory.csv\n"
                        + "8\n");

        assertTrue(output.contains("The file could not be opened."));
    }

    /**
     * Method: testMissingTextFileIsRejected
     * Purpose: Verifies that a nonexistent text file is handled without
     * terminating the application.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testMissingTextFileIsRejected() {
        String missingFile = tempDir.resolve("missing.txt").toString();

        String output = runApplicationWithInput(
                "2\n"
                        + missingFile + "\n"
                        + "8\n");

        assertTrue(output.contains("The file could not be opened."));
        assertTrue(output.contains(
                "Thank you for using the Inventory Management System."));
    }

    /**
     * Method: testValidTextFileCanBeOpened
     * Purpose: Verifies that valid product data can be read from a text file.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testValidTextFileCanBeOpened() throws Exception {
        Path file = tempDir.resolve("inventory.txt");

        Files.writeString(
                file,
                "1234567-Keyboard-Electronics-Logitech-4-25.50\n",
                StandardCharsets.UTF_8);

        String output = runApplicationWithInput(
                "2\n"
                        + file + "\n"
                        + "6\n"
                        + "8\n");

        assertTrue(
                output.contains("File processing complete."),
                "The file was not processed. Output:\n" + output);

        assertTrue(
                output.contains("Items successfully added: 1"),
                "Expected one imported product. Output:\n" + output);

        assertTrue(
                output.contains("Keyboard"),
                "The imported product was not displayed. Output:\n" + output);
    }

    // =========================================================
    // Test 3: Display Data
    // =========================================================

    /**
     * Method: testDisplayEmptyInventory
     * Purpose: Verifies that displaying an empty inventory produces
     * an appropriate message.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testDisplayEmptyInventory() {
        String output = runApplicationWithInput(
                "6\n"
                        + "8\n");

        assertTrue(output.contains(
                "There are currently no items in inventory."));
    }
    // =========================================================
    // Test 4: Create Data
    // =========================================================

    /**
     * Method: testManuallyCreateProduct
     * Purpose: Verifies that a valid product can be added manually.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testManuallyCreateProduct() {
        String input =
                "1\n"
                        + "1234567\n"
                        + "Laptop\n"
                        + "Electronics\n"
                        + "Dell\n"
                        + "5\n"
                        + "799.99\n"
                        + "8\n";

        String output = runApplicationWithInput(input);

        assertTrue(
                output.contains("Item successfully added."),
                "Expected the valid product to be added. Output:\n" + output);
    }

    /**
     * Method: testInvalidProductInputIsRejected
     * Purpose: Verifies that invalid IDs, blank names, negative quantities,
     * and malformed numeric input are rejected before a valid product is added.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testInvalidProductInputIsRejected() {
        String input =
                "1\n"
                        + "123\n"
                        + "1234567\n"
                        + "\n"
                        + "Laptop\n"
                        + "Electronics\n"
                        + "Dell\n"
                        + "-1\n"
                        + "5\n"
                        + "abc\n"
                        + "799.99\n"
                        + "8\n";

        String output = runApplicationWithInput(input);

        assertAll(
                "Invalid product input validation",
                () -> assertTrue(
                        output.contains(
                                "Product ID must contain exactly 7 digits."),
                        "Missing invalid-ID message. Output:\n" + output),

                () -> assertTrue(
                        output.contains("This field cannot be empty."),
                        "Missing blank-name message. Output:\n" + output),

                () -> assertTrue(
                        output.contains(
                                "Quantity must be a non-negative whole number"),
                        "Missing negative-quantity message. Output:\n" + output),

                () -> assertTrue(
                        output.contains("Please enter a valid price.")
                                || output.contains(
                                "Please enter a valid whole number."),
                        "Missing numeric-validation message. Output:\n" + output),

                () -> assertTrue(
                        output.contains("Item successfully added."),
                        "The product was not successfully added. Output:\n" + output)
        );
    }

    // =========================================================
    // Test 7: Display Total Inventory Value
    // =========================================================

    /**
     * Method: testDisplayTotalInventoryValue
     * Purpose: Verifies that the custom action displays the total
     * inventory value when the inventory is empty.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testDisplayTotalInventoryValue() {
        String output = runApplicationWithInput(
                "7\n"
                        + "8\n");

        assertTrue(output.contains("Total Inventory Value"));
        assertTrue(output.contains("$0.00"));
    }

    /**
     * Method: testDisplayCalculatedInventoryValue
     * Purpose: Verifies that the custom action calculates the total
     * value of products using their quantities and unit prices.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testDisplayCalculatedInventoryValue() {
        String input =
                "1\n"
                        + "1234567\n"
                        + "Laptop\n"
                        + "Electronics\n"
                        + "Dell\n"
                        + "2\n"
                        + "100.00\n"
                        + "7\n"
                        + "8\n";

        String output = runApplicationWithInput(input);

        assertTrue(
                output.contains("$200.00"),
                "Expected total inventory value of $200.00. Output:\n" + output);
    }
}
