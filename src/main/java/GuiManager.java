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
     * Scrapes the timetable. Displays a loading message and scraper in the background.
     *
     * @param timeframe               The number of days to look ahead for the timetable.
     * @param login                   The login username.
     * @param password                The login password.
     * @param arg                     Additional argument for the web driver, such as "--headless".
     * @return                        The TimetableData obtained from the scraper.
     * @throws IOException            If an I/O error occurs while reading the configuration file.
     * @throws InterruptedException   If the thread is interrupted while waiting.
     * @throws ExecutionException     If an error occurs during the execution of the timetable scraper.
     */
    public TimetableData getTimetableData(final int timeframe, final String login, final String password, final String arg) throws IOException, InterruptedException, ExecutionException {
        final String[] LOADING_STATES = {"Loading", "Loading.", "Loading..", "Loading..."};
        int loadingId = 0;
        TimetableData ttData;

        @SuppressWarnings("all")
        SwingWorker<TimetableData, Void> scraper = new SwingWorker() {
            @Override
            protected TimetableData doInBackground() {
                return Scraper.timetableScraper(timeframe, login, password, arg);
            }
        };
        JLabel label = new JLabel("Loading");
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        this.panel.add(label);

        scraper.execute();
        while (!scraper.isDone()) {
            label.setText(LOADING_STATES[loadingId]);
            loadingId = loadingId + 1 > 3 ? 0 : loadingId + 1; // avoids modulo, premature optimization
            Thread.sleep(500);
        }
        try {
            ttData = scraper.get();
        } catch (Exception e) {
            throw new ExecutionException("timetableScraper failed.", e.getCause());
        }
        this.panel.remove(label);

        return ttData;
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