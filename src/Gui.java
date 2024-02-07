import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.JTableHeader;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.awt.Color;
import java.awt.Font;

public class Gui {
    private JFrame frame;
    private JPanel panel;

    public Gui() {
        this.frame = new JFrame("Plan lekcji");
        this.panel = new JPanel(new GridBagLayout());
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
        this.frame.setSize(500, 300);
        this.frame.add(panel);
    }


    public void display(final String BINARY_PATH, final String CONFIG_PATH) throws IOException, InterruptedException, ExecutionException {
        String[][] data;
        Timetable tb = new Timetable(BINARY_PATH, CONFIG_PATH);
        if (tb.load() == 0) {
            data = tb.getTimetableData();
        } else {
            data = getData(CONFIG_PATH);
        }
        displayData(data);

    }


    private String[][] getData(String CONFIG_PATH) throws IOException, InterruptedException, ExecutionException {
        final String[] LOADING_STATES = {"Loading", "Loading.", "Loading..", "Loading..."};
        int loading_id = 0;
        Config cfg = new Config(CONFIG_PATH);
        String[][] data;

        @SuppressWarnings("all")
        SwingWorker<List<List<String>>, Void> scraper = new SwingWorker() {
            @Override
            protected List<List<String>> doInBackground() {
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
            data = Timetable.format(scraper.get());
        } catch (Exception e) {
            throw new ExecutionException("timetableScraper failed.", e.getCause());
        }
        this.panel.remove(label);

        return data;
    }

    
    private void displayData(final String[][] DATA) {
        String[] headerData = {"przedmiot", " godzina"};
        JPanel tablePanel = new JPanel();

        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        JTable table = new JTable(DATA, headerData);
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
}