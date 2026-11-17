package IMS;
/**
 * @authorKelly Wilburn
 * Course: CEN 3024 - Software Development 1
 * @since November 16, 2026
 * Tests the Inventory Management System application's entry point.
 * Verifies that {@link Main} declares a standard Java entry point and
 * launches the {@link GUI} Swing window instead of the former console menu.
 * The window-launch test requires a graphical environment and uses JUnit 5
 * and Swing utilities to detect and clean up the application window.
 */

import org.junit.jupiter.api.Test;
import javax.swing.JDialog;

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
     * Verifies that {@link Main} declares a public, static entry point named
     * {@code main} that accepts a {@code String[]} and returns {@code void}.
     *
     * @throws NoSuchMethodException if the standard main method is missing
     * @throws SecurityException if reflective access to Main is denied
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
     * Runs {@link Main#main(String[])} and verifies that it creates a visible
     * {@link GUI} window with the required application title. Skips the test
     * in headless environments and closes startup dialogs and test windows.
     *
     * @throws Exception if Swing event dispatching or window inspection fails
     */
    @Test
    void testMainLaunchesInventoryWindow() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(),

                "IMS.Main window test requires a desktop graphics environment.");

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

            // IMS.Main schedules IMS.GUI construction; this barrier waits for that work.

            SwingUtilities.invokeAndWait(() -> { });

            AtomicReference<GUI> launched = new AtomicReference<>();
            SwingUtilities.invokeAndWait(() -> {
                for (Frame frame : Frame.getFrames()) {
                    if (frame instanceof GUI && frame.isDisplayable()) {
                        launched.set((GUI) frame);
                    }
                }
            });

            assertNotNull(launched.get(), "IMS.Main did not create an inventory IMS.GUI.");

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
