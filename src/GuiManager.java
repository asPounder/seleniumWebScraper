import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.JTableHeader;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.awt.Color;
import java.awt.Font;

/**
 * Class responsible for managing the graphical user interface (GUI) of the application.
 */
public class GuiManager {
    private JFrame frame;
    private JPanel panel;

    /**
     * Constructs a new GUI.
     */
    public GuiManager() {
        this.frame = new JFrame("Plan lekcji");
        this.panel = new JPanel(new GridBagLayout());
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
        this.frame.setSize(500, 300);
        this.frame.add(panel);
    }


    /**
     * Retrieves timetable data from a configuration file.
     *
     * @param configPath The path to the configuration file.
     * @return The timetable data obtained from the configuration file.
     * @throws IOException            If an I/O error occurs while reading the configuration file.
     * @throws InterruptedException   If the thread is interrupted while waiting.
     * @throws ExecutionException     If an error occurs during the execution of the timetable scraper.
     */
    public TimetableData getTimetableData(final String configPath) throws IOException, InterruptedException, ExecutionException {
        final String[] LOADING_STATES = {"Loading", "Loading.", "Loading..", "Loading..."};
        int loading_id = 0;
        ConfigManager cfg = new ConfigManager(configPath);
        TimetableData td;

        @SuppressWarnings("all")
        SwingWorker<TimetableData, Void> scraper = new SwingWorker() {
            @Override
            protected TimetableData doInBackground() {
                return Scraper.timetableScraper(cfg.timeframe, cfg.login, cfg.password, cfg.arg);
            }
        };
        JLabel label = new JLabel("Loading");
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        this.panel.add(label);

        scraper.execute();
        while (!scraper.isDone()) {
            label.setText(LOADING_STATES[loading_id % 4]);
            loading_id += 1;
            Thread.sleep(500);
        }
        try {
            td = scraper.get();
        } catch (Exception e) {
            throw new ExecutionException("timetableScraper failed.", e.getCause());
        }
        this.panel.remove(label);

        return td;
    }

    /**
     * Displays the timetable on the GUI.
     *
     * @param timetable The timetable data to be displayed.
     */
    public void displaytimetable(final String[][] timetable) {
        String[] headerData = {"przedmiot", " godzina"};
        JPanel tablePanel = new JPanel();

        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        JTable table = new JTable(timetable, headerData);
        JTableHeader header = table.getTableHeader();

        Color color = UIManager.getColor("Table.gridColor");
        MatteBorder headerBorder = new MatteBorder(1, 1, 0, 1, color);
        MatteBorder tableBorder = new MatteBorder(0, 1, 1, 1, color);
        table.setBorder(tableBorder);
        header.setBorder(headerBorder);

        table.getColumnModel().getColumn(0).setPreferredWidth(270);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.setRowHeight(30);

        tablePanel.add(header);
        tablePanel.add(table);
        this.panel.add(tablePanel);
        this.panel.revalidate();
        this.panel.repaint();
    }

    /**
     * Closes the GUI.
     */
    public void close() {
        this.frame.dispose();
    }
}