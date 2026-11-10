/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * November 9, 2026
 * Class: GUITest
 * Purpose: Tests the initial Swing interface of the Inventory Management System.
 * Verifies window settings, product fields, table configuration, and the
 * availability of database actions before a database connection is established.
 */

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class GUITest {

    private GUI gui;
    private AWTEventListener dialogCloser;

    /**
     * Method: setUp
     * Purpose: Creates the GUI on the Swing event-dispatch thread and closes
     * the expected startup information popup automatically during tests.
     * Parameters: none.
     * Return: void.
     */
    @BeforeEach
    void setUp() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "GUI tests require a desktop graphics environment.");

        dialogCloser = event -> {
            if (event instanceof WindowEvent
                    && event.getID() == WindowEvent.WINDOW_OPENED
                    && ((WindowEvent) event).getWindow() instanceof JDialog) {
                JDialog dialog = (JDialog) ((WindowEvent) event).getWindow();
                SwingUtilities.invokeLater(dialog::dispose);
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(
                dialogCloser, AWTEvent.WINDOW_EVENT_MASK);

        AtomicReference<GUI> created = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> created.set(new GUI()));
        gui = created.get();
    }

    /**
     * Method: tearDown
     * Purpose: Closes the test window and removes the popup listener so tests
     * do not leave application windows or event listeners running.
     * Parameters: none.
     * Return: void.
     */
    @AfterEach
    void tearDown() throws Exception {
        if (gui != null) {
            SwingUtilities.invokeAndWait(gui::dispose);
        }
        if (dialogCloser != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(dialogCloser);
        }
    }

    /**
     * Method: getField
     * Purpose: Reads a private GUI component for inspection without modifying
     * the application source code.
     * Parameters: name - name of the GUI field; type - expected component class.
     * Return: T - the requested GUI component.
     */
    private <T> T getField(String name, Class<T> type) throws Exception {
        Field field = GUI.class.getDeclaredField(name);
        field.setAccessible(true);
        return type.cast(field.get(gui));
    }

    // =========================================================
    // Test 1: Display the Graphical Interface
    // =========================================================

    /**
     * Method: testWindowTitleAndVisibility
     * Purpose: Verifies that the inventory application opens with the correct title.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testWindowTitleAndVisibility() {
        assertAll("Main GUI window",
                () -> assertEquals("IMS - Inventory Management System", gui.getTitle()),
                () -> assertTrue(gui.isVisible()),
                () -> assertNotNull(gui.getContentPane()));
    }

    /**
     * Method: testProductEntryFieldsExist
     * Purpose: Verifies that all six product input fields and the database path
     * field are initialized when the window opens.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testProductEntryFieldsExist() throws Exception {
        assertAll("Product entry fields",
                () -> assertNotNull(getField("productIdField", JTextField.class)),
                () -> assertNotNull(getField("productNameField", JTextField.class)),
                () -> assertNotNull(getField("categoryField", JTextField.class)),
                () -> assertNotNull(getField("supplierField", JTextField.class)),
                () -> assertNotNull(getField("quantityField", JTextField.class)),
                () -> assertNotNull(getField("priceField", JTextField.class)),
                () -> assertNotNull(getField("databasePathField", JTextField.class)));
    }

    // =========================================================
    // Test 2: Display Inventory Data
    // =========================================================

    /**
     * Method: testInventoryTableStartsEmpty
     * Purpose: Verifies that the inventory table initially has no product rows
     * and contains the six required column headings.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testInventoryTableStartsEmpty() throws Exception {
        JTable table = getField("productTable", JTable.class);
        assertAll("Initial inventory table",
                () -> assertEquals(0, table.getRowCount()),
                () -> assertEquals(6, table.getColumnCount()),
                () -> assertEquals("Product ID", table.getColumnName(0)),
                () -> assertEquals("Product Name", table.getColumnName(1)),
                () -> assertEquals("Category", table.getColumnName(2)),
                () -> assertEquals("Supplier", table.getColumnName(3)),
                () -> assertEquals("Quantity", table.getColumnName(4)),
                () -> assertEquals("Unit Price ($)", table.getColumnName(5)));
    }

    /**
     * Method: testTableAllowsMultipleSelection
     * Purpose: Verifies that multiple inventory rows may be selected for removal.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testTableAllowsMultipleSelection() throws Exception {
        JTable table = getField("productTable", JTable.class);
        assertEquals(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION,
                table.getSelectionModel().getSelectionMode());
    }

    // =========================================================
    // Test 3: Database Connection Requirements
    // =========================================================

    /**
     * Method: testDatabaseActionsDisabledBeforeConnection
     * Purpose: Verifies that inventory operations are unavailable before the
     * user connects to a database.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testDatabaseActionsDisabledBeforeConnection() throws Exception {
        assertAll("Database-dependent actions",
                () -> assertFalse(getField("addButton", JButton.class).isEnabled()),
                () -> assertFalse(getField("updateButton", JButton.class).isEnabled()),
                () -> assertFalse(getField("searchButton", JButton.class).isEnabled()),
                () -> assertFalse(getField("removeButton", JButton.class).isEnabled()),
                () -> assertFalse(getField("valueButton", JButton.class).isEnabled()));
    }

    /**
     * Method: testInitialConnectionInstructions
     * Purpose: Verifies that the status bar instructs users to connect a database.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testInitialConnectionInstructions() throws Exception {
        JLabel status = getField("statusLabel", JLabel.class);
        assertTrue(status.getText().toLowerCase().contains("database"));
    }
}
