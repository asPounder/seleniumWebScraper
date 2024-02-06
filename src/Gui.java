import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.JTableHeader;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.Font;

public class Gui {

    public void start() {
        final String[] LOADING_STATES = {"Loading", "Loading.", "Loading..", "Loading..."};
        int loading_id = 0;
      
        JFrame frame = new JFrame("Plan lekcji");
        JPanel panel = new JPanel(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setSize(500, 300);
        frame.add(panel);
    }
}