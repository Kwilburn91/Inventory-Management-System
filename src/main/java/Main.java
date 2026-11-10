/**
 * Kelly Wilburn
 * CEN 3024 - Software Development 1
 * November 8, 2026
 * Class: Main
 * Purpose: Starts the SQLite-based graphical Inventory Management System.
 * The user selects the SQLite database file from the GUI after the application opens.
 */

import javax.swing.SwingUtilities;

public class Main {

    /**
     * Method: main
     * Purpose: Launches the Inventory Management System GUI on Swing's event-dispatch thread.
     * Parameters: args - command-line arguments (not used).
     * Return: void.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI());
    }
}
