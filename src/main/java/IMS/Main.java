package IMS;
/**
 @authorKelly Wilburn
  * Course: CEN 3024 - Software Development 1
  * @since November 16, 2026
 * Class: IMS.Main
 * Entry point for the graphical Inventory Management System. Launches the Swing IMS.GUI on the event-dispatch thread; the IMS.GUI coordinates user interactions and database access.
 */

import javax.swing.SwingUtilities;

public class Main {

    /**
     * Starts the application IMS.GUI on the Swing event-dispatch thread.
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new GUI());

    }

}
