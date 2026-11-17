package IMS;
/**
 * @author Kelly Wilburn
 * CEN 3024 - Software Development 1
 * @since November 16, 2026
 * * Tests the initial state of the {@link GUI} Swing interface for the
 *  * Inventory Management System. Uses JUnit 5 to verify window properties,
 *  * input components, table configuration, and database-dependent controls.
 *  * The suite constructs the window on Swing's event-dispatch thread and
 *  * uses reflection to inspect otherwise private GUI components; it requires
 *  * a graphical environment and disposes of the window after each test.
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
     * Prepares a fresh GUI for each test by checking for a display, installing
     * a listener that closes startup dialogs, and constructing {@link GUI}
     * on Swing's event-dispatch thread.
     *
     * @throws Exception if the GUI cannot be created on the event-dispatch thread
     */
    @BeforeEach
    void setUp() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(),

                "IMS.GUI tests require a desktop graphics environment.");

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
     * Disposes of the GUI and removes the startup-dialog listener after each
     * test to avoid retaining windows and global event listeners.
     *
     * @throws Exception if disposing of the GUI on the event-dispatch thread fails
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
     * Reads a private GUI field through reflection so its component state can
     * be tested without changing the application's encapsulation.
     *
     * @param name the exact declared name of the GUI field to inspect
     * @param type the expected runtime type of the field's value
     * @param <T> the component or value type to return
     * @return the field's value cast to the expected type
     * @throws NoSuchFieldException if {@code GUI} has no field with that name
     * @throws IllegalAccessException if the field value cannot be read
     * @throws SecurityException if access to the field is denied
     * @throws ClassCastException if the value does not match {@code type}
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
     * Verifies that the main inventory window has the expected title, is
     * visible, and has an initialized content pane.
     */
    @Test
    void testWindowTitleAndVisibility() {

        assertAll("IMS.Main IMS.GUI window",

                () -> assertEquals("IMS - Inventory Management System", gui.getTitle()),
                () -> assertTrue(gui.isVisible()),
                () -> assertNotNull(gui.getContentPane()));
    }

    /**
     * Verifies that the six product-entry text fields and the database-path
     * field are initialized when the GUI is created.
     *
     * @throws Exception if reflection cannot access a required GUI field
     */
    @Test
    void testProductEntryFieldsExist() throws Exception {

        assertAll("IMS.Product entry fields",

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
     * Verifies that the initial inventory table contains no product rows and
     * exposes the six required product column headings.
     *
     * @throws Exception if reflection cannot access the inventory table
     */
    @Test
    void testInventoryTableStartsEmpty() throws Exception {
        JTable table = getField("productTable", JTable.class);
        assertAll("Initial inventory table",
                () -> assertEquals(0, table.getRowCount()),
                () -> assertEquals(6, table.getColumnCount()),

                () -> assertEquals("IMS.Product ID", table.getColumnName(0)),

                () -> assertEquals("IMS.Product Name", table.getColumnName(1)),

                () -> assertEquals("Category", table.getColumnName(2)),
                () -> assertEquals("Supplier", table.getColumnName(3)),
                () -> assertEquals("Quantity", table.getColumnName(4)),
                () -> assertEquals("Unit Price ($)", table.getColumnName(5)));
    }

    /**
     * Verifies that the inventory table allows multiple noncontiguous rows
     * to be selected for a batch removal operation.
     *
     * @throws Exception if reflection cannot access the inventory table
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
     * Verifies that add, update, search, remove, and inventory-value controls
     * remain disabled until a database connection is established.
     *
     * @throws Exception if reflection cannot access the action buttons
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
     * Verifies that the initial status message prompts the user to establish
     * a database connection before performing inventory operations.
     *
     * @throws Exception if reflection cannot access the status label
     */
    @Test
    void testInitialConnectionInstructions() throws Exception {
        JLabel status = getField("statusLabel", JLabel.class);
        assertTrue(status.getText().toLowerCase().contains("database"));
    }
}
