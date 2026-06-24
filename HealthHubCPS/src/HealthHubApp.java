import util.TemaUI;
import vista.LoginFrame;

import javax.swing.SwingUtilities;


public class HealthHubApp {
    public static void main(String[] args) {
        TemaUI.aplicarLookAndFeel();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
