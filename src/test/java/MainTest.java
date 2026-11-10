/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * November 9, 2026
 * Class: MainTest
 * Purpose: Tests the graphical Inventory Management System entry point.
 * Verifies that Main provides the correct startup method and launches the
 * application window through Swing rather than using the former console menu.
 */

import org.junit.jupiter.api.Test;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class MainTest {

    // =========================================================
    // Test 1: Application Entry Point
    // =========================================================

    /**
     * Method: testMainMethodExists
     * Purpose: Verifies that the application exposes the standard public static
     * void main(String[] args) entry point.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testMainMethodExists() throws Exception {
        Method method = Main.class.getDeclaredMethod("main", String[].class);
        assertAll("Application entry point",
                () -> assertTrue(Modifier.isPublic(method.getModifiers())),
                () -> assertTrue(Modifier.isStatic(method.getModifiers())),
                () -> assertEquals(void.class, method.getReturnType()));
    }

    // =========================================================
    // Test 2: Launch the Graphical Interface
    // =========================================================

    /**
     * Method: testMainLaunchesInventoryWindow
     * Purpose: Verifies that running main creates a visible inventory GUI with
     * the expected title. Automatically closes the startup popup and test window.
     * Parameters: none.
     * Return: void.
     */
    @Test
    void testMainLaunchesInventoryWindow() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "Main window test requires a desktop graphics environment.");

        AWTEventListener dialogCloser = event -> {
            if (event instanceof WindowEvent
                    && event.getID() == WindowEvent.WINDOW_OPENED
                    && ((WindowEvent) event).getWindow() instanceof JDialog) {
                JDialog dialog = (JDialog) ((WindowEvent) event).getWindow();
                SwingUtilities.invokeLater(dialog::dispose);
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(
                dialogCloser, AWTEvent.WINDOW_EVENT_MASK);
        try {
            Main.main(new String[0]);

            // Main schedules GUI construction; this barrier waits for that work.
            SwingUtilities.invokeAndWait(() -> { });

            AtomicReference<GUI> launched = new AtomicReference<>();
            SwingUtilities.invokeAndWait(() -> {
                for (Frame frame : Frame.getFrames()) {
                    if (frame instanceof GUI && frame.isDisplayable()) {
                        launched.set((GUI) frame);
                    }
                }
            });

            assertNotNull(launched.get(), "Main did not create an inventory GUI.");
            assertAll("Application startup",
                    () -> assertEquals("IMS - Inventory Management System",
                            launched.get().getTitle()),
                    () -> assertTrue(launched.get().isVisible()));
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                for (Frame frame : Frame.getFrames()) {
                    if (frame instanceof GUI) {
                        frame.dispose();
                    }
                }
            });
            Toolkit.getDefaultToolkit().removeAWTEventListener(dialogCloser);
        }
    }
}
