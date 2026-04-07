import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        setLayout(new BorderLayout());

        JPanel dashboard = switch (LoginScreen.loggedInRole) {
            case "DOCTOR"   -> new DoctorDashboard();
            default         -> new PatientDashboard();
        };

        add(dashboard, BorderLayout.CENTER);
    }
}
