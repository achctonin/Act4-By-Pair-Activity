package frontend;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Starting To-Do List App...");
            new ToDoFrontend();
        });
    }
}